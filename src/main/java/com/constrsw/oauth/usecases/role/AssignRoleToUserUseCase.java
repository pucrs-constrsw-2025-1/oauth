package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.exception.custom_exceptions.UserAlreadyHaveRole;
import com.constrsw.oauth.model.RoleResponse;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.usecases.interfaces.IAssignRoleToUserUseCase;
import com.constrsw.oauth.usecases.user.GetUserByIdUseCase;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignRoleToUserUseCase implements IAssignRoleToUserUseCase {

    private final KeycloakRoleService keycloakRoleService;
    private final GetRoleByIdUseCase getRoleByIdUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetRolesFromUser getRolesFromUser;

    @Override
    public void execute(String userId, String roleId) {
        try {
            UserResponse user = getUserByIdUseCase.execute(userId);
            RoleResponse role = getRoleByIdUseCase.execute(roleId);

            List<RoleRepresentation> userRoles = getRolesFromUser.execute(userId);

            Boolean hasRole = userRoles.stream()
                    .anyMatch(r -> r.getName().equals(role.getName()));

            if (hasRole) {
                throw new UserAlreadyHaveRole();
            }

            keycloakRoleService.assignRoleToUser(userId, role.getName());
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");
        }
    }
}