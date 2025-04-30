package com.constrsw.oauth.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.constrsw.oauth.dto.LoginRequest;
import com.constrsw.oauth.dto.LoginResponse;
import com.constrsw.oauth.service.KeycloakAuthService;



@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private KeycloakAuthService authService;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        System.out.println("tentativa de login");
        LoginResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
