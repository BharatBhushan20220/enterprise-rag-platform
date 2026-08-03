package com.bharat.embedding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmbeddingRequest {

    @NotEmpty
    private List<@NotBlank String> texts;

    private String model = "text-embedding-3-small";
}
