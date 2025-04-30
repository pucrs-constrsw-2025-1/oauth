package com.constrsw.oauth.model;

import lombok.Data;

@Data
public class TokenResponse {
    private String access_token;
    private String refresh_token;
    private int expires_in;
    private String token_type;
    private int refresh_expires_in;
}