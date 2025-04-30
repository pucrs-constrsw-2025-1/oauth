package com.grupo8.oauth.adapter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Value("${KEYCLOAK_CLIENT_ID}")
        private String clientId;

        private static final String[] ENDPOINTS_WITHOUT_AUTH = {
                        "/auth/login",
                        "/auth/refresh-token",
                        "/health",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
        };

        private static final String[] ENDPOINTS_ADMIN_ONLY = {
                        "/users/**",
                        "/groups/**"
        };

        @Bean
        @Primary
        public SecurityFilterChain securityFilterChain(AuthenticationConverter authenticationConverter,
                        HttpSecurity http) throws Exception {
                return http.authorizeHttpRequests(auth -> auth
                                .requestMatchers(ENDPOINTS_WITHOUT_AUTH).permitAll()
                                .requestMatchers(ENDPOINTS_ADMIN_ONLY).hasAuthority("administrator")
                                .anyRequest().authenticated())
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(sessionConfigurer -> sessionConfigurer
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .build();
        }
}