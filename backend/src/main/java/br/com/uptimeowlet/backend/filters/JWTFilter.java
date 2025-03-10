package br.com.uptimeowlet.backend.filters;

import br.com.uptimeowlet.backend.services.I18nService;
import br.com.uptimeowlet.backend.services.TokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static br.com.uptimeowlet.backend.Utils.isNullOrBlank;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    @Value("${application.security.jwt.type:bearer}")
    public String tokenType;

    private final TokenService tokenService;
    private final I18nService i18nService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(!isNullOrBlank(authorization)){
            try {
                if(!authorization.toLowerCase().startsWith(tokenType.toLowerCase())){
                    throw new ServletException(i18nService.getMessage("application.security.invalid_token_type"));
                }
                var jwt = authorization.substring(tokenType.length()).trim();
                if(tokenService.checkIfExpired(jwt)){
                    throw new JwtException(i18nService.getMessage("application.security.token_expired"));
                }
                var login = tokenService.extractLogin(jwt);
                var userDetails = userDetailsService.loadUserByUsername(login);
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ex){
                response.setStatus(ex instanceof JwtException ? HttpStatus.UNAUTHORIZED.value() : HttpStatus.BAD_REQUEST.value());
                response.getOutputStream().print(ex.getMessage());
                response.getOutputStream().flush();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
