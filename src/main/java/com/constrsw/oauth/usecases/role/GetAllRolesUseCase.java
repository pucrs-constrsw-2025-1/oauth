package com.constrsw.oauth.usecases.role;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.RoleResponse;
import com.constrsw.oauth.service.KeycloakRoleService;
import com.constrsw.oauth.usecases.interfaces.IGetAllRolesUseCase;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllRolesUseCase implements IGetAllRolesUseCase {

    private final KeycloakRoleService keycloakRoleService;

    @Override
    public List<RoleResponse> execute() {
        try {
            List<RoleRepresentation> roles = keycloakRoleService.getAllRoles();
            
            return roles.stream()
                    .map(RoleResponse::fromRoleRepresentation)
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "roles");
            return List.of();
        }
    }
}