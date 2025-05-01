package com.constrsw.oauth.usecases.user;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.exception.custom_exceptions.UserNotFoundException;
import com.constrsw.oauth.model.UserResponse;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.IGetUserByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.NotFoundException;

@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase implements IGetUserByIdUseCase {

    private final KeycloakUserService keycloakUserService;

    @Override
    public UserResponse execute(String userId) {
        try {
            UserRepresentation user = keycloakUserService.getUserById(userId);

            return UserResponse.fromUserRepresentation(user);
        }
        catch (NotFoundException e) {
            throw new UserNotFoundException();
        }
        catch (RuntimeException e) {
            GlobalExceptionHandler.handleKeycloakException(e, "users");

            return null;
        }
    }
}