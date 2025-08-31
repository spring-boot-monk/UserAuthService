package com.springbootmonk.UserAuthService.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Auth Service API")
                        .description("Reusable authentication and authorization microservice with JWT + OAuth2 support")
                        .version("1.0.0"));
    }
}

