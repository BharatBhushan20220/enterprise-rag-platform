package com.bharat.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class SearchHitResponse {

    private UUID chunkId;
    private UUID documentId;
    private int chunkIndex;
    private String content;
    private double score;
}
