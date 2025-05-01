package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.exception.custom_exceptions.AlreadyExistsRoleWithSameName;
import com.constrsw.oauth.exception.custom_exceptions.RoleDecriptionIsRequired;
import com.constrsw.oauth.exception.custom_exceptions.RoleNameIsRequiredException;
import com.constrsw.oauth.model.RoleResponse;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.usecases.interfaces.IGetRoleByIdUseCase;
import com.constrsw.oauth.usecases.interfaces.IPatchRoleUseCase;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PatchRoleUseCase implements IPatchRoleUseCase {

    private final IGetRoleByIdUseCase getRoleByIdUseCase;
    private final KeycloakRoleService keycloakRoleService;

    @Override
    public void execute(String roleId, Map<String, Object> updates) {
        try {
            if (updates.get("name") == null) {
                throw new RoleNameIsRequiredException();
            }

            RoleResponse role = getRoleByIdUseCase.execute(roleId);

            if (updates.get("name").equals(role.getName())) {
                throw new AlreadyExistsRoleWithSameName();
            }

            keycloakRoleService.patchRole(role.getName(), updates);
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");
        }
    }
}