package com.grupo8.oauth.application.service;

import com.grupo8.oauth.adapter.keycloak.KeycloakAdapter;
import com.grupo8.oauth.adapter.keycloak.KeycloakMapper;
import com.grupo8.oauth.application.DTOs.UserDTO;
import com.grupo8.oauth.application.DTOs.UserRequestDTO;
import com.grupo8.oauth.application.exception.InvalidEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class CreateUser {
    private static final String VALID_EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    private final KeycloakAdapter keycloakAdapter;

    private final KeycloakMapper keycloakMapper;

    public UserDTO run(String authorizationHeader, UserRequestDTO user) {
        Pattern pattern = Pattern.compile(VALID_EMAIL_REGEX);
        if (!pattern.matcher(user.username()).matches()) {
            throw new InvalidEmailException(user.username());
        }
        return keycloakMapper.toUserDTO(
                keycloakAdapter.createUser(authorizationHeader, keycloakMapper.toKeycloakUserRegistration(user)));
    }
}
