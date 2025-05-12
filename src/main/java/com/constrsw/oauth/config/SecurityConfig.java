package com.constrsw.oauth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/users/**") .authenticated() // Protege todos os endpoints em /users/**
                    .requestMatchers("/api/auth/login").permitAll() // Permite acesso ao endpoint de login
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Permite acesso ao Swagger UI e API docs
                    .requestMatchers("/manage/health").permitAll() // Permite acesso ao endpoint de health check do Actuator
                    .anyRequest().authenticated() // Qualquer outra requisição precisa de autenticação
            )
            .oauth2ResourceServer(oauth2ResourceServer ->
                oauth2ResourceServer.jwt(jwt -> {})
            )
            .sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(csrf -> csrf.disable()); // Desabilitar CSRF é comum para APIs stateless

        return http.build() ;
    }
}