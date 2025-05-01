package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.usecases.interfaces.IGetRolesFromUser;
import com.constrsw.oauth.usecases.interfaces.IGetUserByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetRolesFromUser implements IGetRolesFromUser {

    private final IGetUserByIdUseCase getUserByIdUseCase;
    private final KeycloakRoleService keycloakRoleService;

    @Override
    public List<RoleRepresentation> execute(String userId) {
        try {
            UserResponse user = getUserByIdUseCase.execute(userId);

            return keycloakRoleService.getUserRoles(user.getId());
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");

            return null;
        }
    }
}
