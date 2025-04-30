package com.grupo8.oauth.adapter.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    @Operation(summary = "Verificação de saúde do serviço")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}