package com.constrsw.oauth.usecases.user;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.IGetAllUsersUseCase;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase implements IGetAllUsersUseCase {

    private final KeycloakUserService keycloakUserService;

    @Override
    public List<UserResponse> execute(Boolean enabled) {
        try {
            List<UserRepresentation> users = keycloakUserService.getAllUsers();

            if (users == null || users.isEmpty()) {
                return Collections.emptyList();
            }

            if (enabled != null) {
                users = users.stream()
                        .filter(user -> user.isEnabled() == enabled)
                        .collect(Collectors.toList());
            }

            return users.stream()
                    .map(UserResponse::fromUserRepresentation)
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "users");

            return null;
        }
    }
}