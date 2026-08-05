package com.bharat.auth.config;

import com.bharat.common.openapi.OpenApiFactory;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authOpenApi() {
        return OpenApiFactory.create(
                "Auth Service API",
                "Authentication, registration, token refresh, password reset, and admin user APIs.");
    }
}
