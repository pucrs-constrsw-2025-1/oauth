package com.grupo1.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Setter
public class TokenResponse {
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("refresh_expires_in")
    private Long refreshExpiresIn;
}

