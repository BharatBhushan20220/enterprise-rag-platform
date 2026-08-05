package com.bharat.common.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Shared OpenAPI / Swagger document factory for servlet microservices.
 */
public final class OpenApiFactory {

    private OpenApiFactory() {
    }

    public static OpenAPI create(String title, String description) {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version("1.0.0")
                        .contact(new Contact().name("Enterprise RAG Platform").email("admin@example.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token from /api/v1/auth/login")));
    }
}
