package com.bharat.document.service;

import com.bharat.common.exception.ResourceNotFoundException;
import com.bharat.document.dto.DocumentResponse;
import com.bharat.document.dto.DocumentUploadRequest;
import com.bharat.document.entity.Document;
import com.bharat.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;

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
}
