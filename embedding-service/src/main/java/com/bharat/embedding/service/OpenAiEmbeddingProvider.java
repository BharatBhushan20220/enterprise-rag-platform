package com.bharat.embedding.service;

import com.bharat.common.exception.BadRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "embedding.provider", havingValue = "openai")
public class OpenAiEmbeddingProvider implements EmbeddingProvider {

    private final RestClient restClient;
    private final String modelName;
    private final int dimensions;

    public OpenAiEmbeddingProvider(
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.base-url:https://api.openai.com}") String baseUrl,
            @Value("${embedding.model:text-embedding-3-small}") String modelName,
            @Value("${embedding.dimensions:1536}") int dimensions) {
        this.modelName = modelName;
        this.dimensions = dimensions;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public String modelName() {
        return modelName;
    }

    @Override
    public int dimensions() {
        return dimensions;
    }

    @Override
    public List<List<Float>> embed(List<String> texts) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("input", texts);

        JsonNode response = restClient.post()
                .uri("/v1/embeddings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.path("data").isArray()) {
            throw new BadRequestException("OpenAI embedding response was invalid");
        }

        List<List<Float>> vectors = new ArrayList<>();
        for (JsonNode item : response.path("data")) {
            List<Float> vector = new ArrayList<>();
            item.path("embedding").forEach(value -> vector.add(value.floatValue()));
            vectors.add(vector);
        }
        return vectors;
    }
}
