package com.constrsw.oauth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityAutoConfiguration {

    /**
     * Configura um UserDetailsService básico para evitar que o Spring Security
     * gere uma senha aleatória durante o desenvolvimento.
     */
    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN", "USER_MANAGER")
                .build();
        
        UserDetails userManager = User.builder()
                .username("manager")
                .password(passwordEncoder.encode("manager"))
                .roles("USER_MANAGER")
                .build();
        
        return new InMemoryUserDetailsManager(adminUser, userManager);
    }
}