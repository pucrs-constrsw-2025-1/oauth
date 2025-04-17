package com.constrsw.oauth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String tokenType;
    private String accessToken;
    private Integer expiresIn;
    private String refreshToken;
    private Integer refreshExpiresIn;
}