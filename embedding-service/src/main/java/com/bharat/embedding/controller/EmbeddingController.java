package com.bharat.embedding.controller;

import com.bharat.common.response.ApiResponse;
import com.bharat.embedding.dto.EmbeddingRequest;
import com.bharat.embedding.dto.EmbeddingResponse;
import com.bharat.embedding.service.EmbeddingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/embeddings")
@RequiredArgsConstructor
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    @PostMapping
    public ApiResponse<EmbeddingResponse> embed(@Valid @RequestBody EmbeddingRequest request) {
        return ApiResponse.ok(embeddingService.embed(request), "Embeddings generated");
    }
}
