package com.bharat.embedding.controller;

import com.bharat.common.response.ApiResponse;
import com.bharat.embedding.dto.EmbeddingRequest;
import com.bharat.embedding.dto.EmbeddingResponse;
import com.bharat.embedding.service.EmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/embeddings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Embeddings", description = "Generate text embeddings")
@SecurityRequirement(name = "bearerAuth")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    @PostMapping
    @Operation(summary = "Generate embeddings for input texts")
    public ApiResponse<EmbeddingResponse> embed(@Valid @RequestBody EmbeddingRequest request) {
        log.info("Embedding request texts={}", request.getTexts() != null ? request.getTexts().size() : 0);
        return ApiResponse.ok(embeddingService.embed(request), "Embeddings generated");
    }
}
