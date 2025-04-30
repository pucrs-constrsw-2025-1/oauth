package com.grupo8.oauth.adapter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Value("")
        private static final String RESOURCE_ACCESS_CLAIM = "resource_access";
        private static final String ROLES_CLAIM = "roles";

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
                                .oauth2ResourceServer(resourceServer -> resourceServer
                                                .jwt(jwtResourceServer -> jwtResourceServer
                                                                .jwtAuthenticationConverter(
                                                                                (Converter<Jwt, ? extends AbstractAuthenticationToken>) authenticationConverter)))
                                .build();
        }

        @Bean
        @SuppressWarnings("unchecked")
        public AuthoritiesConverter authoritiesConverter() {
                return jwt -> {
                        var realmAccess = (Map<String, Object>) jwt.getClaims().getOrDefault(RESOURCE_ACCESS_CLAIM,
                                        Map.of());
                        var clientData = (Map<String, Object>) realmAccess.getOrDefault(clientId, Map.of());
                        var roles = (List<String>) clientData.getOrDefault(ROLES_CLAIM, List.of());

                        return roles.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .toList();
                };
        }

        @Bean
        public Converter<Jwt, AbstractAuthenticationToken> authenticationConverter(
                        AuthoritiesConverter authoritiesConverter) {
                return jwt -> new JwtAuthenticationToken(
                                jwt,
                                authoritiesConverter.convert(jwt),
                                jwt.getClaimAsString(StandardClaimNames.PREFERRED_USERNAME));
        }
}