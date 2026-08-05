package com.bharat.embedding.config;

import com.bharat.common.openapi.OpenApiFactory;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI embeddingOpenApi() {
        return OpenApiFactory.create(
                "Embedding Service API",
                "Text embedding generation (stub or OpenAI providers).");
    }
}
