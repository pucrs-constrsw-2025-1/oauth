package org.firpy.oauth;

import me.paulschwarz.springdotenv.DotenvPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
@EnableFeignClients
public class OauthApplication
{
    public static void main(String[] args)
    {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        DotenvPropertySource.addToEnvironment(applicationContext.getEnvironment());

        applicationContext.refresh();

        SpringApplication.run(OauthApplication.class, args);
    }
}
