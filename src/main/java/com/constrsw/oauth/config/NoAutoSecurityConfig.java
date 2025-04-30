package com.constrsw.oauth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração para desabilitar a geração automática de senha do Spring Security
 */
@Configuration
public class NoAutoSecurityConfig {

    /**
     * Define a propriedade de segurança para desabilitar a geração de senha automática
     */
    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityProperties securityProperties() {
        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.getUser().setPassword(null);
        return securityProperties;
    }
}