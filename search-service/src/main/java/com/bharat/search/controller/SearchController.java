package com.bharat.search.controller;

import com.bharat.common.response.ApiResponse;
import com.bharat.search.dto.IndexChunkRequest;
import com.bharat.search.dto.SearchHitResponse;
import com.bharat.search.dto.SearchRequest;
import com.bharat.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Search", description = "Index chunks and run similarity/keyword search")
@SecurityRequirement(name = "bearerAuth")
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/index")
    @Operation(summary = "Index a document chunk with embedding")
    public ApiResponse<SearchHitResponse> index(@Valid @RequestBody IndexChunkRequest request) {
        log.info("Indexing chunk documentId={} index={}", request.getDocumentId(), request.getChunkIndex());
        return ApiResponse.ok(searchService.index(request), "Chunk indexed");
    }

    @PostMapping
    @Operation(summary = "Search indexed chunks")
    public ApiResponse<List<SearchHitResponse>> search(@Valid @RequestBody SearchRequest request) {
        log.info("Search topK={}", request.getTopK());
        return ApiResponse.ok(searchService.search(request), "Search completed");
    }
}
