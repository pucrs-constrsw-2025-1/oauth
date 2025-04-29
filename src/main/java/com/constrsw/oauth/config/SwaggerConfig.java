package com.constrsw.oauth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${keycloak.server.url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${OAUTH_INTERNAL_PORT:8080}")
    private String port;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createInfo())
                .servers(createServers())
                .tags(createTags())
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token obtido através do endpoint de login")));
    }

    private Info createInfo() {
        return new Info()
                .title("OAuth Service API")
                .version("1.0")
                .description("API para autenticação e gerenciamento de usuários/roles com Keycloak")
                .contact(new Contact()
                        .name("Equipe de Desenvolvimento")
                        .email("dev@constrsw.com")
                        .url("https://constrsw.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"))
                .termsOfService("https://constrsw.com/terms");
    }

    private List<Server> createServers() {
        Server localServer = new Server()
                .url("http://localhost:" + port)
                .description("Servidor local");

        Server dockerServer = new Server()
                .url("http://oauth:" + port)
                .description("Servidor Docker");

        return Arrays.asList(localServer, dockerServer);
    }

    private List<Tag> createTags() {
        Tag authTag = new Tag()
                .name("Authentication")
                .description("Endpoints para autenticação de usuários");

        Tag usersTag = new Tag()
                .name("Users")
                .description("Endpoints para gerenciamento de usuários");

        Tag rolesTag = new Tag()
                .name("Roles")
                .description("Endpoints para gerenciamento de roles");

        Tag userRolesTag = new Tag()
                .name("User Roles")
                .description("Endpoints para gerenciamento de associação entre usuários e roles");

        return Arrays.asList(authTag, usersTag, rolesTag, userRolesTag);
    }
}