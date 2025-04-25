package com.grupo1.oauth.configuration;

import com.grupo1.oauth.service.KeycloakService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockKeycloakServiceConfig {
    @Bean
    public KeycloakService keycloakService() {
        return Mockito.mock(KeycloakService.class);
    }
}
