package com.constrsw.oauth.usecases.user;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.IUpdatePasswordUseCase;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.NotFoundException;

@Service
@RequiredArgsConstructor
public class UpdatePasswordUseCase implements IUpdatePasswordUseCase {

    private final KeycloakUserService keycloakUserService;

    @Override
    public void execute(String userId, String newPassword) {
        try {
            UserRepresentation user = keycloakUserService.getUserById(userId);

            keycloakUserService.resetPassword(userId, newPassword);
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "users");
        }
    }
}