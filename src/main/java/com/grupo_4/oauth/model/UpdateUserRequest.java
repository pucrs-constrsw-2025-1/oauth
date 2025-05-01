package com.grupo_4.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled = true;
    private boolean emailVerified = false;
} 