package br.com.uptimeowlet.backend;

import br.com.uptimeowlet.backend.exceptions.RefreshTokenException;
import br.com.uptimeowlet.backend.models.User;
import br.com.uptimeowlet.backend.repositories.TokenRepository;
import br.com.uptimeowlet.backend.services.I18nService;
import br.com.uptimeowlet.backend.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @InjectMocks
    private I18nService i18nService;

    @Autowired
    private TokenRepository tokenRepository;

    @InjectMocks
    private User user;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(tokenService, "tokenType", "bearer");
        ReflectionTestUtils.setField(tokenService, "issuer", "uptime-owlet");
        ReflectionTestUtils.setField(tokenService, "signatureSecret", "emVvMEl+eEXCo0VHfSnCo1l1cHJeVjJYX2tCRzdSb0d4R2Y/RUhGYD1jVFI2YV05bEdHZA==");
        ReflectionTestUtils.setField(tokenService, "lifeTime", 1800);
        ReflectionTestUtils.setField(tokenService, "repository", tokenRepository);
        ReflectionTestUtils.setField(i18nService, "messageSource", Mockito.mock(MessageSource.class));
        ReflectionTestUtils.setField(tokenService, "i18nService", i18nService);
        ReflectionTestUtils.setField(user, "id", 1);
        ReflectionTestUtils.setField(user, "login", "admin");
    }

    @Order(1)
    @Test
    void shouldCreateToken() {
        var token = tokenService.create(user);
        assertNotNull(token);
        assertEquals("bearer", token.type());
        assertNotNull(token.value());
        assertTrue(ZonedDateTime.now().isBefore(token.expiration()));
    }

    @Order(2)
    @Test
    void shouldExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(tokenService, "lifeTime", 1);
        var token = tokenService.create(user);
        assertNotNull(token);
        Thread.sleep(Duration.ofSeconds(2));
        var expired = tokenService.checkIfExpired(token.value());
        assertTrue(expired);
    }

    @Order(1)
    @Test
    void shouldNotExpiredToken() {
        var token = tokenService.create(user);
        assertNotNull(token);
        assertFalse(tokenService.checkIfExpired(token.value()));
    }

    @Order(1)
    @Test
    void shouldRefreshToken() {
        var token = tokenService.create(user);
        assertNotNull(token);
        var newToken = tokenService.refresh(token.value(), token.key());
        assertNotNull(newToken);

        assertNotEquals(token.value(), newToken.value());
        assertNotEquals(token.key(), newToken.key());
        assertTrue(token.expiration().isBefore(newToken.expiration()));
    }

    @Order(1)
    @Test
    void shouldNotRefreshTokenTwice() {
        Mockito.when(i18nService.getMessage("application.token.already_refreshed"))
                .thenReturn("Token already refreshed.");

        var token = tokenService.create(user);
        assertNotNull(token);
        var newToken = tokenService.refresh(token.value(), token.key());
        assertNotNull(newToken);

        var exception = assertThrows(RefreshTokenException.class,
                () -> tokenService.refresh(token.value(), token.key()));
        assertEquals("Token already refreshed.", exception.getMessage());
    }
}
