package com.bharat.document.controller;

import com.bharat.common.response.ApiResponse;
import com.bharat.document.dto.DocumentResponse;
import com.bharat.document.dto.DocumentUploadRequest;
import com.bharat.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Documents", description = "Document registration, upload, and listing")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    @Operation(summary = "Register document metadata")
    public ApiResponse<DocumentResponse> register(@Valid @RequestBody DocumentUploadRequest request) {
        log.info("Registering document title={}", request.getTitle());
        return ApiResponse.ok(documentService.upload(request), "Document registered successfully");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file, parse, chunk, and index")
    public ApiResponse<DocumentResponse> uploadAndIndex(
            @RequestParam(value = "title", required = false) String title,
            @RequestPart("file") MultipartFile file) {
        log.info("Uploading document fileName={} title={}", file.getOriginalFilename(), title);
        return ApiResponse.ok(documentService.uploadAndIndex(title, file), "Document uploaded and indexed");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by id")
    public ApiResponse<DocumentResponse> getById(@PathVariable UUID id) {
        return ApiResponse.ok(documentService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List documents")
    public ApiResponse<List<DocumentResponse>> list() {
        return ApiResponse.ok(documentService.list());
    }
}
