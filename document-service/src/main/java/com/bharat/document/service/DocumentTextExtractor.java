package com.bharat.document.service;

import com.bharat.common.exception.BadRequestException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class DocumentTextExtractor {

    public String extract(MultipartFile file) {
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        String fileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);

        try {
            if (contentType.contains("pdf") || fileName.endsWith(".pdf")) {
                return extractPdf(file.getBytes());
            }
            if (contentType.startsWith("text/") || fileName.endsWith(".txt") || fileName.endsWith(".md")) {
                return new String(file.getBytes(), StandardCharsets.UTF_8);
            }
            throw new BadRequestException("Unsupported file type. Upload PDF or plain text.");
        } catch (IOException ex) {
            throw new BadRequestException("Failed to read uploaded file", ex);
        }
    }

    private String extractPdf(byte[] bytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            if (text == null || text.isBlank()) {
                throw new BadRequestException("PDF contained no extractable text");
            }
            return text;
        }
    }
}
