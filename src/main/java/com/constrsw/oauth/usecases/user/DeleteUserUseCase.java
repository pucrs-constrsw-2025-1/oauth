package com.constrsw.oauth.usecases.user;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.IDeleteUserUseCase;
import com.constrsw.oauth.usecases.interfaces.IGetUserByIdUseCase;
import jakarta.ws.rs.NotFoundException;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeleteUserUseCase implements IDeleteUserUseCase {

    private final KeycloakUserService keycloakUserService;
    private final IGetUserByIdUseCase getUserByIdUseCase;

    public void execute(String userId) {
        try {
            UserResponse user = getUserByIdUseCase.execute(userId);

            keycloakUserService.deleteUser(user.getId());
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "users");
        }
    }
}
