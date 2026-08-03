package com.bharat.search.service;

import com.bharat.common.exception.BadRequestException;
import com.bharat.common.util.VectorMath;
import com.bharat.search.dto.IndexChunkRequest;
import com.bharat.search.dto.SearchHitResponse;
import com.bharat.search.dto.SearchRequest;
import com.bharat.search.entity.SearchChunk;
import com.bharat.search.repository.SearchChunkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchChunkRepository searchChunkRepository;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    public SearchHitResponse index(IndexChunkRequest request) {
        if (request.getEmbedding() == null || request.getEmbedding().isEmpty()) {
            throw new BadRequestException("embedding vector is required");
        }

        SearchChunk chunk = new SearchChunk();
        chunk.setDocumentId(request.getDocumentId());
        chunk.setChunkIndex(request.getChunkIndex());
        chunk.setContent(request.getContent());
        chunk.setEmbeddingModel(request.getEmbeddingModel());
        chunk.setEmbeddingJson(writeEmbedding(request.getEmbedding()));
        SearchChunk saved = searchChunkRepository.save(chunk);

        trySyncPgVector(saved.getId(), request.getEmbedding());
        return toHit(saved, 1.0);
    }

    @Transactional(readOnly = true)
    public List<SearchHitResponse> search(SearchRequest request) {
        List<SearchChunk> chunks = searchChunkRepository.findAll();
        if (request.getQueryEmbedding() != null && !request.getQueryEmbedding().isEmpty()) {
            return chunks.stream()
                    .map(chunk -> toHit(chunk, VectorMath.cosineSimilarity(request.getQueryEmbedding(), readEmbedding(chunk.getEmbeddingJson()))))
                    .sorted(Comparator.comparingDouble(SearchHitResponse::getScore).reversed())
                    .limit(request.getTopK())
                    .toList();
        }

        if (request.getQuery() == null || request.getQuery().isBlank()) {
            throw new BadRequestException("Either query or queryEmbedding is required");
        }

        String query = request.getQuery().toLowerCase(Locale.ROOT);
        return chunks.stream()
                .filter(chunk -> chunk.getContent().toLowerCase(Locale.ROOT).contains(query))
                .map(chunk -> toHit(chunk, keywordScore(chunk.getContent(), query)))
                .sorted(Comparator.comparingDouble(SearchHitResponse::getScore).reversed())
                .limit(request.getTopK())
                .toList();
    }

    private void trySyncPgVector(java.util.UUID id, List<Float> embedding) {
        try {
            String literal = toPgVectorLiteral(embedding);
            jdbcTemplate.update("UPDATE search_chunks SET embedding = CAST(? AS vector) WHERE id = ?", literal, id);
        } catch (Exception ignored) {
            // pgvector column may not exist in local/H2 environments
        }
    }

    private String toPgVectorLiteral(List<Float> embedding) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < embedding.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(embedding.get(i));
        }
        builder.append(']');
        return builder.toString();
    }

    private SearchHitResponse toHit(SearchChunk chunk, double score) {
        return SearchHitResponse.builder()
                .chunkId(chunk.getId())
                .documentId(chunk.getDocumentId())
                .chunkIndex(chunk.getChunkIndex())
                .content(chunk.getContent())
                .score(score)
                .build();
    }

    private double keywordScore(String content, String query) {
        String lowerContent = content.toLowerCase(Locale.ROOT);
        if (!lowerContent.contains(query)) {
            return 0.0;
        }
        return Math.min(1.0, (double) query.length() / Math.max(1, lowerContent.length()) * 10);
    }

    private String writeEmbedding(List<Float> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (JsonProcessingException ex) {
            throw new BadRequestException("Unable to serialize embedding", ex);
        }
    }

    private List<Float> readEmbedding(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException ex) {
            return List.of();
        }
    }
}
