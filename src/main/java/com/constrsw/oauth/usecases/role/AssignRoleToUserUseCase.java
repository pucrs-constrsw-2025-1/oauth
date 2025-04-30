package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.IAssignRoleToUserUseCase;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignRoleToUserUseCase implements IAssignRoleToUserUseCase {

    private final KeycloakRoleService keycloakRoleService;
    private final KeycloakUserService keycloakUserService;

    @Override
    public void execute(String userId, String roleId) {
        try {
            UserRepresentation user = keycloakUserService.getUserById(userId);
            if (user == null) {
                throw new NotFoundException("Usuário não encontrado com o ID: " + userId);
            }

            RoleRepresentation role = keycloakRoleService.getRoleById(roleId);
            if (role == null) {
                throw new NotFoundException("Role não encontrada com o ID: " + roleId);
            }

            List<RoleRepresentation> userRoles = keycloakRoleService.getUserRoles(userId);
            boolean hasRole = userRoles.stream()
                    .anyMatch(r -> r.getName().equals(role.getName()));

            if (hasRole) {
                return;
            }

            keycloakRoleService.assignRoleToUser(userId, roleId);
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");
        }
    }
}