package com.bharat.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gatewayOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise RAG API Gateway")
                        .description("""
                                Aggregated Swagger UI for platform microservices.
                                Use the definition dropdown to switch between Auth, Documents, Embeddings, Search, and Chat.
                                Secure endpoints require a Bearer JWT from Auth login.
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("Enterprise RAG Platform").email("admin@example.com")));
    }
}
