package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.exception.custom_exceptions.AlreadyExistsRoleWithSameName;
import com.constrsw.oauth.exception.custom_exceptions.RoleDecriptionIsRequired;
import com.constrsw.oauth.exception.custom_exceptions.RoleNameIsRequiredException;
import com.constrsw.oauth.model.RoleRequest;
import com.constrsw.oauth.model.RoleResponse;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.usecases.interfaces.IGetRoleByIdUseCase;
import com.constrsw.oauth.usecases.interfaces.IUpdateRoleUseCase;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateRoleUseCase implements IUpdateRoleUseCase {

    private final IGetRoleByIdUseCase getRoleByIdUseCase;
    private final KeycloakRoleService keycloakRoleService;

    @Override
    public void execute(String roleId, RoleRequest roleRequest) {
        try {
            if (roleRequest.getName() == null || roleRequest.getName().trim().isEmpty()) {
                throw new RoleNameIsRequiredException();
            }

            if (roleRequest.getDescription() == null) {
                throw new RoleDecriptionIsRequired();
            }
            
            RoleResponse role = getRoleByIdUseCase.execute(roleId);

            if (roleRequest.getName().equals(role.getName())) {
                throw new AlreadyExistsRoleWithSameName();
            }
            
            keycloakRoleService.updateRole(
                    role.getName(),
                    roleRequest.getName(), 
                    roleRequest.getDescription());
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");
        }
    }
}