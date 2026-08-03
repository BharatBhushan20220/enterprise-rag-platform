package com.bharat.search.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class IndexChunkRequest {

    @NotNull
    private UUID documentId;

    @NotNull
    private Integer chunkIndex;

    @NotBlank
    private String content;

    private String embeddingModel = "text-embedding-3-small";

    private List<Float> embedding = new ArrayList<>();
}
