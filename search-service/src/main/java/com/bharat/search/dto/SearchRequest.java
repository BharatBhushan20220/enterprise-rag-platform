package com.bharat.search.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SearchRequest {

    private String query;

    private List<Float> queryEmbedding = new ArrayList<>();

    @Min(1)
    @Max(50)
    private int topK = 5;
}
