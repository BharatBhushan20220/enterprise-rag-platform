package com.bharat.search.repository;

import com.bharat.search.entity.SearchChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SearchChunkRepository extends JpaRepository<SearchChunk, UUID> {

    @Query("""
            select c from SearchChunk c
            where lower(c.content) like lower(concat('%', :query, '%'))
            order by c.chunkIndex asc
            """)
    List<SearchChunk> searchByContent(@Param("query") String query);
}
