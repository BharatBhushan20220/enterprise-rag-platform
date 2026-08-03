package com.bharat.document.controller;

import com.bharat.common.response.ApiResponse;
import com.bharat.document.dto.DocumentResponse;
import com.bharat.document.dto.DocumentUploadRequest;
import com.bharat.document.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ApiResponse<DocumentResponse> register(@Valid @RequestBody DocumentUploadRequest request) {
        return ApiResponse.ok(documentService.upload(request), "Document registered successfully");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DocumentResponse> uploadAndIndex(
            @RequestParam(value = "title", required = false) String title,
            @RequestPart("file") MultipartFile file) {
        return ApiResponse.ok(documentService.uploadAndIndex(title, file), "Document uploaded and indexed");
    }

    @GetMapping("/{id}")
    public ApiResponse<DocumentResponse> getById(@PathVariable UUID id) {
        return ApiResponse.ok(documentService.getById(id));
    }

    @GetMapping
    public ApiResponse<List<DocumentResponse>> list() {
        return ApiResponse.ok(documentService.list());
    }
}
