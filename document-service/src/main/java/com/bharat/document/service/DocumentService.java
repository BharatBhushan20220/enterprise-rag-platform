package com.bharat.document.service;

import com.bharat.common.exception.BadRequestException;
import com.bharat.common.exception.ResourceNotFoundException;
import com.bharat.document.dto.DocumentResponse;
import com.bharat.document.dto.DocumentUploadRequest;
import com.bharat.document.entity.Document;
import com.bharat.document.repository.DocumentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentTextExtractor documentTextExtractor;
    private final TextChunkingService textChunkingService;
    private final RestClient embeddingRestClient;
    private final RestClient searchRestClient;
    private final Path storageRoot;
    private final String embeddingModel;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentTextExtractor documentTextExtractor,
            TextChunkingService textChunkingService,
            @Qualifier("embeddingRestClient") RestClient embeddingRestClient,
            @Qualifier("searchRestClient") RestClient searchRestClient,
            @Value("${document.storage-path:./data/documents}") String storagePath,
            @Value("${rag.embedding-model:text-embedding-3-small}") String embeddingModel) {
        this.documentRepository = documentRepository;
        this.documentTextExtractor = documentTextExtractor;
        this.textChunkingService = textChunkingService;
        this.embeddingRestClient = embeddingRestClient;
        this.searchRestClient = searchRestClient;
        this.storageRoot = Path.of(storagePath);
        this.embeddingModel = embeddingModel;
    }

    @Transactional
    public DocumentResponse upload(DocumentUploadRequest request) {
        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setFileName(request.getFileName());
        document.setContentType(request.getContentType());
        document.setSizeBytes(request.getSizeBytes());
        document.setStoragePath(request.getStoragePath());
        document.setStatus(Document.DocumentStatus.UPLOADED);
        return DocumentResponse.from(documentRepository.save(document));
    }

    @Transactional
    public DocumentResponse uploadAndIndex(String title, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        Document document = new Document();
        document.setTitle(title == null || title.isBlank() ? file.getOriginalFilename() : title);
        document.setFileName(file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename());
        document.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        document.setSizeBytes(file.getSize());
        document.setStoragePath("pending");
        document.setStatus(Document.DocumentStatus.PROCESSING);
        document = documentRepository.save(document);

        try {
            Path target = storageRoot.resolve(document.getId() + "-" + document.getFileName());
            Files.createDirectories(storageRoot);
            Files.write(target, file.getBytes());
            document.setStoragePath(target.toAbsolutePath().toString());

            String text = documentTextExtractor.extract(file);
            document.setExtractedText(text);
            List<String> chunks = textChunkingService.chunk(text);
            if (chunks.isEmpty()) {
                throw new BadRequestException("No text chunks produced from document");
            }

            List<List<Float>> embeddings = embed(chunks);
            for (int i = 0; i < chunks.size(); i++) {
                indexChunk(document.getId(), i, chunks.get(i), embeddings.get(i));
            }

            document.setChunkCount(chunks.size());
            document.setStatus(Document.DocumentStatus.INDEXED);
            return DocumentResponse.from(documentRepository.save(document));
        } catch (RuntimeException ex) {
            document.setStatus(Document.DocumentStatus.FAILED);
            documentRepository.save(document);
            throw ex;
        } catch (Exception ex) {
            document.setStatus(Document.DocumentStatus.FAILED);
            documentRepository.save(document);
            throw new BadRequestException("Failed to process document", ex);
        }
    }

    @Transactional(readOnly = true)
    public DocumentResponse getById(UUID id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
        return DocumentResponse.from(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> list() {
        return documentRepository.findAll().stream().map(DocumentResponse::from).toList();
    }

    @SuppressWarnings("unchecked")
    private List<List<Float>> embed(List<String> chunks) {
        Map<String, Object> body = new HashMap<>();
        body.put("texts", chunks);
        body.put("model", embeddingModel);

        JsonNode response = embeddingRestClient.post()
                .uri("/api/v1/embeddings")
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        if (response == null || !response.path("data").path("embeddings").isArray()) {
            throw new BadRequestException("Embedding service returned an invalid response");
        }

        List<List<Float>> vectors = new java.util.ArrayList<>();
        for (JsonNode vectorNode : response.path("data").path("embeddings")) {
            List<Float> vector = new java.util.ArrayList<>();
            vectorNode.forEach(value -> vector.add(value.floatValue()));
            vectors.add(vector);
        }
        if (vectors.size() != chunks.size()) {
            throw new BadRequestException("Embedding count mismatch");
        }
        return vectors;
    }

    private void indexChunk(UUID documentId, int chunkIndex, String content, List<Float> embedding) {
        Map<String, Object> body = new HashMap<>();
        body.put("documentId", documentId.toString());
        body.put("chunkIndex", chunkIndex);
        body.put("content", content);
        body.put("embeddingModel", embeddingModel);
        body.put("embedding", embedding);

        searchRestClient.post()
                .uri("/api/v1/search/index")
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
