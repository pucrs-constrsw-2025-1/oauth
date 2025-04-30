package com.constrsw.oauth.usecases.user;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.exception.user_exceptions.InvalidEmailFormatException;
import com.constrsw.oauth.exception.user_exceptions.NullUserDataException;
import com.constrsw.oauth.exception.user_exceptions.PasswordEmptyException;
import com.constrsw.oauth.exception.user_exceptions.UserIdRetrievalException;
import com.constrsw.oauth.exception.user_exceptions.UsernameConflictException;
import com.constrsw.oauth.exception.user_exceptions.UsernameEmptyException;
import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.ICreateUserUseCase;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.apache.james.mime4j.dom.Entity;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase implements ICreateUserUseCase {

    private final KeycloakUserService keycloakUserService;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @Override
    public String execute(UserRequest userRequest, boolean isTemporary) {
        try {
            List<UserRepresentation> existingUsers = keycloakUserService.getUserByUsername(userRequest.getUsername());
            if (!existingUsers.isEmpty()) {
                throw new UsernameConflictException(userRequest.getUsername());
            }

            validateUserRequest(userRequest);

            Response response = keycloakUserService.createUser(userRequest, isTemporary);
            Object entity = response.readEntity(String.class);

            return processUserCreationResponse(response);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private void validateUserRequest(UserRequest userRequest) {
        if (userRequest == null) {
            throw new NullUserDataException();
        }

        String username = userRequest.getUsername();
        String password = userRequest.getPassword();

        if (username == null || username.isBlank()) {
            throw new UsernameEmptyException();
        }

        if (password == null || password.isBlank()) {
            throw new PasswordEmptyException();
        }

//        if (!username.matches(EMAIL_REGEX)) {
//            throw new InvalidEmailFormatException();
//        }
    }

    private String processUserCreationResponse(Response response) {
        int status = response.getStatus();

        if (status == Response.Status.CREATED.getStatusCode()) {
            String location = response.getHeaderString("Location");
            return Optional.ofNullable(location)
                    .map(loc -> loc.substring(loc.lastIndexOf('/') + 1))
                    .orElseThrow(UserIdRetrievalException::new);
        }

        GlobalExceptionHandler.handleKeycloakException(
                new jakarta.ws.rs.WebApplicationException(response),
                "users"
        );

        return null;
    }
}