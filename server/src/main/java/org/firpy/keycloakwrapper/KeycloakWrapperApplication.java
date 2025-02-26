package org.firpy.keycloakwrapper;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class KeycloakWrapperApplication
{
    public static void main(String[] args)
    {
        Dotenv dotenv = Dotenv.configure()
                              .ignoreIfMissing()
                              .load();

        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(KeycloakWrapperApplication.class, args);
    }
}
