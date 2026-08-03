package com.bharat.embedding.service;

import java.util.List;

public interface EmbeddingProvider {

    String modelName();

    int dimensions();

    List<List<Float>> embed(List<String> texts);
}
