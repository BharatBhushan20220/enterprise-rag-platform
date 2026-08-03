package com.bharat.document.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TextChunkingServiceTest {

    @Test
    void chunk_shouldSplitLongTextWithOverlap() {
        TextChunkingService service = new TextChunkingService(20, 5);
        String text = "abcdefghijklmnopqrstuvwxyz0123456789";

        List<String> chunks = service.chunk(text);

        assertThat(chunks).hasSizeGreaterThan(1);
        assertThat(chunks.get(0)).hasSize(20);
    }

    @Test
    void chunk_shouldReturnSingleChunkForShortText() {
        TextChunkingService service = new TextChunkingService(100, 10);
        assertThat(service.chunk("short text")).containsExactly("short text");
    }
}
