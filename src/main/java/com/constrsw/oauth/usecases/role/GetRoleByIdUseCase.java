package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.exception.custom_exceptions.RoleNotFoundException;
import com.constrsw.oauth.model.RoleResponse;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.usecases.interfaces.IGetRoleByIdUseCase;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetRoleByIdUseCase implements IGetRoleByIdUseCase {

    private final KeycloakRoleService keycloakRoleService;

    @Override
    public RoleResponse execute(String roleId) {
        try {
            RoleRepresentation role = keycloakRoleService.getRoleById(roleId);

            if (role == null) {
                throw new RoleNotFoundException(roleId);
            }
            
            return RoleResponse.fromRoleRepresentation(role);
        }
        catch (RoleNotFoundException e) {
            throw e;
        }
        catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");

            return null;
        }
    }
}