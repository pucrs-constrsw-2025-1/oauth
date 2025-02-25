package org.firpy.keycloakwrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
public class KeycloakWrapperApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(KeycloakWrapperApplication.class, args);
    }
}
