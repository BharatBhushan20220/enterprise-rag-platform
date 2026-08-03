package com.bharat.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentUploadRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String fileName;

    @NotBlank
    private String contentType;

    @NotNull
    @PositiveOrZero
    private Long sizeBytes;

    @NotBlank
    private String storagePath;
}
