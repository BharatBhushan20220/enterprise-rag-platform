package com.bharat.search.config;

import com.bharat.common.openapi.OpenApiFactory;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI searchOpenApi() {
        return OpenApiFactory.create(
                "Search Service API",
                "Chunk indexing and vector/keyword search (JSON cosine or pgvector).");
    }
}
