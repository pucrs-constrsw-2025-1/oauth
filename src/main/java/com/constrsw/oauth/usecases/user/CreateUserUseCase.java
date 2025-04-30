package com.constrsw.oauth.usecases.user;

import com.constrsw.oauth.exception.GlobalExceptionHandler;
import com.constrsw.oauth.exception.custom_exceptions.InvalidEmailFormatException;
import com.constrsw.oauth.exception.custom_exceptions.NullUserDataException;
import com.constrsw.oauth.exception.custom_exceptions.PasswordEmptyException;
import com.constrsw.oauth.exception.custom_exceptions.UserIdRetrievalException;
import com.constrsw.oauth.exception.custom_exceptions.UsernameConflictException;
import com.constrsw.oauth.exception.custom_exceptions.UsernameEmptyException;
import com.constrsw.oauth.model.UserRequest;
import com.constrsw.oauth.service.KeycloakUserService;
import com.constrsw.oauth.usecases.interfaces.ICreateUserUseCase;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase implements ICreateUserUseCase {

    private final KeycloakUserService keycloakUserService;

    private static final String EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

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

        if (!EMAIL_PATTERN.matcher(username).matches()) {
            throw new InvalidEmailFormatException();
        }
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

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
