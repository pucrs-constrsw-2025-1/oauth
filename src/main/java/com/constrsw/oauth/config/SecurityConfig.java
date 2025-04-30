package com.constrsw.oauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * 🔐 SecurityConfig
 *
 * Esta classe centraliza a configuração de segurança da aplicação Spring Boot.
 * Ela adota práticas modernas, como autenticação stateless e a separação clara
 * entre endpoints públicos e protegidos.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Variáveis injetadas via application.properties ou application.yml
     * Permitem a customização do usuário em memória sem expor dados sensíveis no código.
     */
    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    /**
     * Conjunto de endpoints que não exigem autenticação.
     * Inclui interfaces de documentação, monitoramento e login.
     */
    private static final String[] PUBLIC_ENDPOINTS = {
        "/login",
        "/health",
        "/manage/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/swagger-resources/**",
        "/webjars/**"
    };

    /**
     * 🔧 Configura o filtro principal de segurança da aplicação.
     * 
     * - Desabilita CSRF para APIs RESTful (sem estado).
     * - Define política stateless para sessões.
     * - Permite acesso irrestrito a endpoints públicos.
     * - Libera todos os acessos temporariamente para facilitar desenvolvimento.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .anyRequest().permitAll() // Substitua por .authenticated() em produção
            );

        return http.build();
    }

    /**
     * 🔐 Encoder responsável pela criptografia de senhas.
     * Utiliza o algoritmo BCrypt, amplamente recomendado pela comunidade de segurança.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 👤 UserDetailsService em memória.
     * Cria dinamicamente um usuário com base nas propriedades fornecidas no arquivo de configuração.
     * Útil para ambientes de teste ou prototipagem.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username(username)
            .password(passwordEncoder().encode(password))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user);
    }
}
