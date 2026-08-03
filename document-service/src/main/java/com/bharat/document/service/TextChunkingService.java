package com.bharat.document.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkingService {

    private final int chunkSize;
    private final int chunkOverlap;

    public TextChunkingService(
            @Value("${document.chunk-size:800}") int chunkSize,
            @Value("${document.chunk-overlap:120}") int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = Math.min(chunkOverlap, Math.max(0, chunkSize - 1));
    }

    public List<String> chunk(String text) {
        String normalized = text == null ? "" : text.replaceAll("\\s+", " ").trim();
        if (normalized.isEmpty()) {
            return List.of();
        }
        if (normalized.length() <= chunkSize) {
            return List.of(normalized);
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < normalized.length()) {
            int end = Math.min(normalized.length(), start + chunkSize);
            chunks.add(normalized.substring(start, end).trim());
            if (end >= normalized.length()) {
                break;
            }
            start = Math.max(0, end - chunkOverlap);
        }
        return chunks.stream().filter(chunk -> !chunk.isBlank()).toList();
    }
}
