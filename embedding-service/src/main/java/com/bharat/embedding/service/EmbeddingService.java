package com.bharat.embedding.service;

import com.bharat.embedding.dto.EmbeddingRequest;
import com.bharat.embedding.dto.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * Deterministic placeholder embedding generator for local development.
 * Replace with a real model client (OpenAI / local ONNX) in production.
 */
@Service
public class EmbeddingService {

    @Value("${embedding.dimensions:384}")
    private int dimensions;

    public EmbeddingResponse embed(EmbeddingRequest request) {
        List<List<Float>> vectors = new ArrayList<>();
        for (String text : request.getTexts()) {
            vectors.add(toVector(text));
        }
        return EmbeddingResponse.builder()
                .model(request.getModel())
                .dimensions(dimensions)
                .embeddings(vectors)
                .build();
    }

    private List<Float> toVector(String text) {
        CRC32 crc = new CRC32();
        crc.update(text.getBytes(StandardCharsets.UTF_8));
        long seed = crc.getValue();

        List<Float> vector = new ArrayList<>(dimensions);
        for (int i = 0; i < dimensions; i++) {
            seed = (seed * 1103515245L + 12345L) & 0x7fffffffL;
            float value = ((seed % 10000) / 5000.0f) - 1.0f;
            vector.add(value);
        }
        return normalize(vector);
    }

    private List<Float> normalize(List<Float> vector) {
        double sumSquares = 0.0;
        for (Float value : vector) {
            sumSquares += value * value;
        }
        double norm = Math.sqrt(sumSquares);
        if (norm == 0.0) {
            return vector;
        }
        List<Float> normalized = new ArrayList<>(vector.size());
        for (Float value : vector) {
            normalized.add((float) (value / norm));
        }
        return normalized;
    }
}
