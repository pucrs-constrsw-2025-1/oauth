package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.service.KeycloakRoleService;
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

    private final KeycloakRoleService keycloakRoleService;

    @Override
    public void execute(String roleId, Map<String, Object> updates) {
        try {
            RoleRepresentation role = keycloakRoleService.getRoleById(roleId);
            if (role == null) {
                throw new NotFoundException("Role não encontrada com o ID: " + roleId);
            }

            if (updates.containsKey("name")) {
                String newName = (String) updates.get("name");
                if (!newName.equals(role.getName()) &&
                        keycloakRoleService.getRoleById(newName) != null) {
                    throw new BadRequestException("Já existe uma role com o nome: " + newName);
                }
            }

            keycloakRoleService.patchRole(roleId, updates);
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");
        }
    }
}