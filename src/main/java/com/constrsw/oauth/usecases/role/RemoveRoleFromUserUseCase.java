package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.exception.custom_exceptions.UserDontHaveSelectedRole;
import com.constrsw.oauth.model.RoleResponse;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.IGetRoleByIdUseCase;
import com.constrsw.oauth.usecases.interfaces.IGetRolesFromUser;
import com.constrsw.oauth.usecases.interfaces.IGetUserByIdUseCase;
import com.constrsw.oauth.usecases.interfaces.IRemoveRoleFromUserUseCase;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RemoveRoleFromUserUseCase implements IRemoveRoleFromUserUseCase {

    private final KeycloakRoleService keycloakRoleService;
    private final IGetRoleByIdUseCase getRoleByIdUseCase;
    private final IGetUserByIdUseCase getUserByIdUseCase;
    private final IGetRolesFromUser getRolesFromUser;

    @Override
    public void execute(String userId, String roleId) {
        try {
            UserResponse user = getUserByIdUseCase.execute(userId);
            RoleResponse role = getRoleByIdUseCase.execute(roleId);

            List<RoleRepresentation> userRoles = getRolesFromUser.execute(userId);

            Boolean hasRole = userRoles.stream()
                    .anyMatch(r -> r.getName().equals(role.getName()));

            if (!hasRole) {
                throw new UserDontHaveSelectedRole();
            }

            keycloakRoleService.removeRoleFromUser(userId, roleId);
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");
        }
    }
}