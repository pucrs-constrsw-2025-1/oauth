package com.constrsw.oauth.infrastructure.adapter.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HealthCheckAdapter implements HealthIndicator {
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public HealthCheckAdapter() {
        this.restTemplate = new RestTemplate();
    }
    
    @Override
    public Health health() {
        try {
            // Basic health check - can be extended to check Keycloak connectivity, etc.
            return Health.up()
                    .withDetail("service", "OAuth API")
                    .withDetail("status", "Available")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("service", "OAuth API")
                    .withDetail("status", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}