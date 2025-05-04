package com.constrsw.oauth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/health").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/manage/**").permitAll()
                // Qualquer outra requisição deve ser autenticada, mas não requeremos roles específicas
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        
        return http.build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return jwtConverter;
    }
    
    // Classe personalizada para converter as roles do Keycloak para o formato do Spring Security
    static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            
            // Extrair roles do realm_access
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) realmAccess.get("roles");
                authorities.addAll(
                    roles.stream()
                        .map(roleName -> "ROLE_" + roleName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
                );
            }
            
            // Extrair roles do resource_access para o client específico (opcional)
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            if (resourceAccess != null) {
                // Para cada cliente no resource_access
                resourceAccess.forEach((clientId, clientRoles) -> {
                    if (clientRoles instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> clientRolesMap = (Map<String, Object>) clientRoles;
                        if (clientRolesMap.containsKey("roles")) {
                            @SuppressWarnings("unchecked")
                            List<String> roles = (List<String>) clientRolesMap.get("roles");
                            authorities.addAll(
                                roles.stream()
                                    .map(roleName -> "ROLE_" + clientId + "_" + roleName)
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())
                            );
                        }
                    }
                });
            }
            
            return authorities;
        }
    }
}