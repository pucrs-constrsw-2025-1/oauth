package com.constrsw.oauth.usecases.user;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.service.KeycloakUserService;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeleteUserUseCase {
    private final KeycloakUserService keycloakUserService;

    public void execute(String id) {
        try {
            UserRepresentation userRepresentation = keycloakUserService.getUserById(id);

            if (userRepresentation == null) {
                throw new NotFoundException("Usuário não encontrado com o ID: " + id);
            }

            keycloakUserService.deleteUser(id);
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "users");
        }
    }
}
