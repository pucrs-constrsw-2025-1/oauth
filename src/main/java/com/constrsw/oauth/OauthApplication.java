package com.constrsw.oauth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "OAuth API",
                version = "1.0",
                description = "REST API for OAuth operations using Keycloak",
                contact = @Contact(
                        name = "PUCRS",
                        email = "admin@pucrs.br",
                        url = "https://www.pucrs.br"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OauthApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }
}
