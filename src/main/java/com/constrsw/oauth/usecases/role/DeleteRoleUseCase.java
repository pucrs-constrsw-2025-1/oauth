package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.RoleResponse;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.usecases.interfaces.IDeleteRoleUseCase;
import com.constrsw.oauth.usecases.interfaces.IGetRoleByIdUseCase;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteRoleUseCase implements IDeleteRoleUseCase {

    private final IGetRoleByIdUseCase getRoleByIdUseCase;
    private final KeycloakRoleService keycloakRoleService;

    @Override
    public void execute(String roleId) {
        try {
            RoleResponse role = getRoleByIdUseCase.execute(roleId);

            keycloakRoleService.deleteRole(role.getName());
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");
        }
    }
}