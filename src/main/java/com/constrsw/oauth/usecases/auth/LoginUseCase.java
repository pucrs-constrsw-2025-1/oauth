package com.constrsw.oauth.usecases.auth;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.exception.user_exceptions.InvalidPasswordException;
import com.constrsw.oauth.exception.user_exceptions.PasswordEmptyException;
import com.constrsw.oauth.exception.user_exceptions.UsernameEmptyException;
import com.constrsw.oauth.model.TokenResponse;
import com.constrsw.oauth.service.KeycloakAuthService;
import com.constrsw.oauth.usecases.interfaces.ILoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class LoginUseCase implements ILoginUseCase {

    private final KeycloakAuthService keycloakAuthService;

    @Override
    public TokenResponse execute(String username, String password) {
        try {
            if (username == null || username.isBlank()) {
                throw new UsernameEmptyException();
            }

            if (password == null || password.isBlank()) {
                throw new PasswordEmptyException();
            }

            return keycloakAuthService.authenticate(username, password);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED ||
                    (e.getStatusCode() == HttpStatus.BAD_REQUEST &&
                            e.getResponseBodyAsString().contains("invalid_grant"))) {
                throw new InvalidPasswordException();
            }
            GlobalExceptionHandler.handleKeycloakException(e, "authentication");
            return  null;
        }
    }
}