package com.grupo8.oauth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
