package com.constrsw.oauth.usecases.user;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.IUpdateUserUseCase;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.NotFoundException;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase implements IUpdateUserUseCase {

    private final KeycloakUserService keycloakUserService;

    @Override
    public void execute(String id, UserRequest user) {
        try {
            UserRepresentation userRepresentation = keycloakUserService.getUserById(id);

            userRepresentation.setUsername(user.getUsername());
            userRepresentation.setFirstName(user.getFirstName());
            userRepresentation.setLastName(user.getLastName());

            keycloakUserService.updateUser(userRepresentation);

            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                keycloakUserService.resetPassword(id, user.getPassword());
            }
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "users");
        }
    }
}