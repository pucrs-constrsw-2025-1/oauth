package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.exception.custom_exceptions.AlreadyExistsRoleWithSameName;
import com.constrsw.oauth.exception.custom_exceptions.RoleNameIsRequiredException;
import com.constrsw.oauth.model.RoleRequest;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.usecases.interfaces.ICreateRoleUseCase;
import com.constrsw.oauth.usecases.interfaces.IGetRoleByIdUseCase;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase implements ICreateRoleUseCase {

    private final KeycloakRoleService keycloakRoleService;

    @Override
    public void execute(RoleRequest roleRequest) {
        try {
            if (roleRequest.getName() == null || roleRequest.getName().trim().isEmpty()) {
                throw new RoleNameIsRequiredException();
            }

            if (keycloakRoleService.getRoleById(roleRequest.getName()) != null) {
                throw new AlreadyExistsRoleWithSameName();
            }

            keycloakRoleService.createRole(
                    roleRequest.getName(), 
                    roleRequest.getDescription());
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");
        }
    }
}