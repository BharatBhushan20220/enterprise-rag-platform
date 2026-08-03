package com.bharat.embedding.service;

import com.bharat.embedding.dto.EmbeddingRequest;
import com.bharat.embedding.dto.EmbeddingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingProvider embeddingProvider;

    public EmbeddingResponse embed(EmbeddingRequest request) {
        String model = request.getModel() == null || request.getModel().isBlank()
                ? embeddingProvider.modelName()
                : request.getModel();
        return EmbeddingResponse.builder()
                .model(model)
                .dimensions(embeddingProvider.dimensions())
                .embeddings(embeddingProvider.embed(request.getTexts()))
                .build();
    }
}
