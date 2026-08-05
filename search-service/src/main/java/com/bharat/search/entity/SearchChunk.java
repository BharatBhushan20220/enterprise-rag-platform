package com.bharat.search.entity;

import com.bharat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "search_chunks")
public class SearchChunk extends BaseEntity {

    @Column(nullable = false)
    private UUID documentId;

    @Column(nullable = false)
    private int chunkIndex;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(nullable = false)
    private String embeddingModel;

    /** PostgreSQL TEXT — avoid @Lob (maps to oid/CLOB and fails validate). */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String embeddingJson;
}
