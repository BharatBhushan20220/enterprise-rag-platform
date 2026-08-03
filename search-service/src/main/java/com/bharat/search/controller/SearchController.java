package com.bharat.search.controller;

import com.bharat.common.response.ApiResponse;
import com.bharat.search.dto.IndexChunkRequest;
import com.bharat.search.dto.SearchHitResponse;
import com.bharat.search.dto.SearchRequest;
import com.bharat.search.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/index")
    public ApiResponse<SearchHitResponse> index(@Valid @RequestBody IndexChunkRequest request) {
        return ApiResponse.ok(searchService.index(request), "Chunk indexed");
    }

    @PostMapping
    public ApiResponse<List<SearchHitResponse>> search(@Valid @RequestBody SearchRequest request) {
        return ApiResponse.ok(searchService.search(request), "Search completed");
    }
}
