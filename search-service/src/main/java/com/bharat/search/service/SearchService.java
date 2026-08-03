package com.bharat.search.service;

import com.bharat.search.dto.IndexChunkRequest;
import com.bharat.search.dto.SearchHitResponse;
import com.bharat.search.dto.SearchRequest;
import com.bharat.search.entity.SearchChunk;
import com.bharat.search.repository.SearchChunkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchService {

    private final SearchChunkRepository searchChunkRepository;

    public SearchHitResponse index(IndexChunkRequest request) {
        SearchChunk chunk = new SearchChunk();
        chunk.setDocumentId(request.getDocumentId());
        chunk.setChunkIndex(request.getChunkIndex());
        chunk.setContent(request.getContent());
        chunk.setEmbeddingModel(request.getEmbeddingModel());
        SearchChunk saved = searchChunkRepository.save(chunk);
        return toHit(saved, 1.0);
    }

    @Transactional(readOnly = true)
    public List<SearchHitResponse> search(SearchRequest request) {
        return searchChunkRepository.searchByContent(request.getQuery()).stream()
                .limit(request.getTopK())
                .map(chunk -> toHit(chunk, score(chunk.getContent(), request.getQuery())))
                .toList();
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

    private double score(String content, String query) {
        String lowerContent = content.toLowerCase(Locale.ROOT);
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        if (!lowerContent.contains(lowerQuery)) {
            return 0.0;
        }
        return Math.min(1.0, (double) lowerQuery.length() / Math.max(1, lowerContent.length()) * 10);
    }
}
