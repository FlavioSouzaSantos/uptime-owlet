package br.com.uptimeowlet.backend.services;

import br.com.uptimeowlet.backend.exceptions.RefreshTokenException;
import br.com.uptimeowlet.backend.models.Token;
import br.com.uptimeowlet.backend.models.User;
import br.com.uptimeowlet.backend.records.TokenOutput;
import br.com.uptimeowlet.backend.repositories.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${application.security.jwt.type:bearer}")
    public String tokenType;

    @Value("${application.security.jwt.issuer:uptime-owlet}")
    public String issuer;

    @Value("${application.security.jwt.signature-secret:}")
    public String signatureSecret;

    @Value("${application.security.jwt.lifeTime:1800}")
    private long lifeTime;

    private final TokenRepository repository;
    private final I18nService i18nService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public TokenOutput create(User user) {
        return create(user.getLogin());
    }

    private TokenOutput create(String login) {
        var id = UUID.randomUUID().toString();
        var renovationKey = UUID.randomUUID().toString();
        var expiration = ZonedDateTime.now().plus(Duration.ofSeconds(lifeTime));

        var value = Jwts.builder()
                .id(id)
                .subject(login)
                .issuer(issuer)
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .expiration(Date.from(expiration.toInstant()))
                .signWith(hmacKey())
                .compact();

        var token = Token.builder()
                .tokenId(id)
                .renovationKey(renovationKey)
                .build();

        repository.save(token);

        return new TokenOutput(tokenType, value, renovationKey, expiration);
    }

    private Key hmacKey(){
        return new SecretKeySpec(Base64.getDecoder().decode(signatureSecret),
                HS256.getJcaName());
    }

    public boolean checkIfExpired(TokenOutput tokenOutput) {
        try {
            getClaims(tokenOutput.value(), false);
            return false;
        } catch (ExpiredJwtException ex){
            return true;
        }
    }

    public TokenOutput refresh(String jwt, String key) {
        var claims = getClaims(jwt, true);

        var timeExceeded = Duration.between(claims.getExpiration().toInstant(), ZonedDateTime.now().toInstant());
        if(timeExceeded.toMinutes() > 30){
            throw new RefreshTokenException(i18nService.getMessage("application.token.not_allowed_refresh_after_time", 30));
        }

        var currentToken = repository.findFirstByTokenId(claims.getId())
                .orElseThrow(() -> new RefreshTokenException(i18nService.getMessage("application.token.already_refreshed")));

        if(!key.equals(currentToken.getRenovationKey())){
            throw new RefreshTokenException(i18nService.getMessage("application.token.invalid_key_for_refresh"));
        }
        repository.deleteById(currentToken.getId());

        return create(claims.getSubject());
    }

    private Claims getClaims(String jwt, boolean ignoreExpirationException){
        try {
            return Jwts.parser()
                    .setSigningKey(hmacKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getPayload();
        }catch(Exception ex){
            if(ignoreExpirationException && ex instanceof ExpiredJwtException)
                return ((ExpiredJwtException) ex).getClaims();
            throw ex;
        }
    }
}
