package com.bharat.document.entity;

import com.bharat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "documents")
public class Document extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false)
    private String storagePath;

    @Lob
    @Column
    private String extractedText;

    @Column(nullable = false)
    private int chunkCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status = DocumentStatus.UPLOADED;

    public enum DocumentStatus {
        UPLOADED,
        PROCESSING,
        INDEXED,
        FAILED
    }
}
