package com.bharat.chat.config;

import com.bharat.common.openapi.OpenApiFactory;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI chatOpenApi() {
        return OpenApiFactory.create(
                "Chat Service API",
                "RAG ask endpoints and chat session history.");
    }
}
