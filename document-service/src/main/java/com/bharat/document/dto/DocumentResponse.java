package com.bharat.document.dto;

import com.bharat.document.entity.Document;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DocumentResponse {

    private UUID id;
    private String title;
    private String fileName;
    private String contentType;
    private long sizeBytes;
    private Document.DocumentStatus status;

    public static DocumentResponse from(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .fileName(document.getFileName())
                .contentType(document.getContentType())
                .sizeBytes(document.getSizeBytes())
                .status(document.getStatus())
                .build();
    }
}
