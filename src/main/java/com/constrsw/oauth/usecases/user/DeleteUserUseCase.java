package com.constrsw.oauth.usecases.user;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.IDeleteUserUseCase;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeleteUserUseCase implements IDeleteUserUseCase {
    private final KeycloakUserService keycloakUserService;

    public void execute(String userId) {
        try {
            UserRepresentation userRepresentation = keycloakUserService.getUserById(userId);

            keycloakUserService.deleteUser(userId);
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "users");
        }
    }
}
