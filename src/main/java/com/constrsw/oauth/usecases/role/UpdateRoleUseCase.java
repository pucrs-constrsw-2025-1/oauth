package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.RoleRequest;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.usecases.interfaces.IUpdateRoleUseCase;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateRoleUseCase implements IUpdateRoleUseCase {

    private final KeycloakRoleService keycloakRoleService;

    @Override
    public void execute(String roleId, RoleRequest roleRequest) {
        try {
            if (roleRequest.getName() == null || roleRequest.getName().trim().isEmpty()) {
                throw new BadRequestException("O nome da role é obrigatório");
            }
            
            RoleRepresentation role = keycloakRoleService.getRoleById(roleId);
            if (role == null) {
                throw new NotFoundException("Role não encontrada com o ID: " + roleId);
            }
            
            if (!roleRequest.getName().equals(role.getName()) &&
                keycloakRoleService.getRoleById(roleRequest.getName()) != null) {
                throw new BadRequestException("Já existe uma role com o nome: " + roleRequest.getName());
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