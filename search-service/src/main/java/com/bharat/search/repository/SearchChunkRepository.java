package com.bharat.search.repository;

import com.bharat.search.entity.SearchChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SearchChunkRepository extends JpaRepository<SearchChunk, UUID> {
}
