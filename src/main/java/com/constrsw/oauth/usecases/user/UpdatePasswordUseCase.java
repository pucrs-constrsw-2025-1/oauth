package com.constrsw.oauth.usecases.user;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.RoleResponse;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.IGetUserByIdUseCase;
import com.constrsw.oauth.usecases.interfaces.IUpdatePasswordUseCase;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.NotFoundException;

@Service
@RequiredArgsConstructor
public class UpdatePasswordUseCase implements IUpdatePasswordUseCase {

    private final KeycloakUserService keycloakUserService;
    private final IGetUserByIdUseCase getUserByIdUseCase;

    @Override
    public void execute(String userId, String newPassword) {
        try {
            UserResponse user = getUserByIdUseCase.execute(userId);

            keycloakUserService.resetPassword(user.getId(), newPassword);
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "users");
        }
    }
}