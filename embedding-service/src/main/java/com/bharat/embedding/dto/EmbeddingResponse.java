package com.bharat.embedding.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmbeddingResponse {

    private String model;
    private int dimensions;
    private List<List<Float>> embeddings;
}
