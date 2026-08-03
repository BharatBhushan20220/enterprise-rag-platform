package com.bharat.search.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequest {

    @NotBlank
    private String query;

    @Min(1)
    @Max(50)
    private int topK = 5;
}
