package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.RoleRequest;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.usecases.interfaces.ICreateRoleUseCase;
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
                throw new BadRequestException("O nome da role é obrigatório");
            }
            
            if (keycloakRoleService.getRoleById(roleRequest.getName()) != null) {
                throw new BadRequestException("Já existe uma role com o nome: " + roleRequest.getName());
            }
            
            keycloakRoleService.createRole(
                    roleRequest.getName(), 
                    roleRequest.getDescription());
        } catch (RuntimeException e) {
            System.out.println("Caiu aqui " + e.getMessage());
            GlobalExceptionHandler.handleKeycloakException(e, "roles");
        }
    }
}