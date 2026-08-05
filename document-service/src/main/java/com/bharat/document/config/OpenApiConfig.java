package com.bharat.document.config;

import com.bharat.common.openapi.OpenApiFactory;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentOpenApi() {
        return OpenApiFactory.create(
                "Document Service API",
                "Document upload, parsing, chunking, and indexing endpoints.");
    }
}
