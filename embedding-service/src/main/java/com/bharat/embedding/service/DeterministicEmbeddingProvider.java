package com.bharat.embedding.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

@Component
@ConditionalOnProperty(name = "embedding.provider", havingValue = "stub", matchIfMissing = true)
public class DeterministicEmbeddingProvider implements EmbeddingProvider {

    private final int dimensions;
    private final String modelName;

    public DeterministicEmbeddingProvider(
            @Value("${embedding.dimensions:384}") int dimensions,
            @Value("${embedding.model:text-embedding-3-small}") String modelName) {
        this.dimensions = dimensions;
        this.modelName = modelName;
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
        List<List<Float>> vectors = new ArrayList<>();
        for (String text : texts) {
            vectors.add(toVector(text));
        }
        return vectors;
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
