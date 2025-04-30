package com.grupo8.oauth.application.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRequestDTO(
                String username,
                String password,
                @JsonProperty("first-name") String firstName,
                @JsonProperty("last-name") String lastName,
                Boolean enabled) {
}
