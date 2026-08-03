package com.bharat.chat.service;

import com.bharat.chat.dto.ChatAskRequest;
import com.bharat.chat.dto.ChatAskResponse;
import com.bharat.chat.entity.ChatMessage;
import com.bharat.chat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final RestClient searchRestClient;
    private final RestClient embeddingRestClient;
    private final LlmProvider llmProvider;
    private final String embeddingModel;

    public ChatService(
            ChatMessageRepository chatMessageRepository,
            @Qualifier("searchRestClient") RestClient searchRestClient,
            @Qualifier("embeddingRestClient") RestClient embeddingRestClient,
            LlmProvider llmProvider,
            @Value("${rag.embedding-model:text-embedding-3-small}") String embeddingModel) {
        this.chatMessageRepository = chatMessageRepository;
        this.searchRestClient = searchRestClient;
        this.embeddingRestClient = embeddingRestClient;
        this.llmProvider = llmProvider;
        this.embeddingModel = embeddingModel;
    }

    public ChatAskResponse ask(ChatAskRequest request) {
        List<String> sources = retrieveSources(request.getQuestion());
        String systemPrompt = "You are an enterprise knowledge assistant. Answer only using the provided context. "
                + "If context is insufficient, say you do not know.";
        String userPrompt = buildUserPrompt(request.getQuestion(), sources);
        String answer = llmProvider.generate(systemPrompt, userPrompt);

        ChatMessage message = new ChatMessage();
        message.setSessionId(request.getSessionId());
        message.setQuestion(request.getQuestion());
        message.setAnswer(answer);
        message.setSources(String.join("\n", sources));
        ChatMessage saved = chatMessageRepository.save(message);

        return ChatAskResponse.builder()
                .messageId(saved.getId())
                .sessionId(saved.getSessionId())
                .question(saved.getQuestion())
                .answer(saved.getAnswer())
                .sources(sources)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ChatAskResponse> history(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreatedDateAsc(sessionId).stream()
                .map(message -> ChatAskResponse.builder()
                        .messageId(message.getId())
                        .sessionId(message.getSessionId())
                        .question(message.getQuestion())
                        .answer(message.getAnswer())
                        .sources(message.getSources() == null || message.getSources().isBlank()
                                ? List.of()
                                : List.of(message.getSources().split("\n")))
                        .build())
                .toList();
    }

    private List<String> retrieveSources(String question) {
        try {
            List<Float> queryEmbedding = embedQuery(question);
            Map<String, Object> body = new HashMap<>();
            body.put("query", question);
            body.put("queryEmbedding", queryEmbedding);
            body.put("topK", 3);

            JsonNode response = searchRestClient.post()
                    .uri("/api/v1/search")
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null || response.path("data").isMissingNode()) {
                return List.of();
            }

            List<String> sources = new ArrayList<>();
            for (JsonNode hit : response.path("data")) {
                sources.add(hit.path("content").asText());
            }
            return sources;
        } catch (Exception ex) {
            log.warn("Search retrieval unavailable, answering without sources: {}", ex.getMessage());
            return List.of();
        }
    }

    private List<Float> embedQuery(String question) {
        Map<String, Object> body = Map.of(
                "texts", List.of(question),
                "model", embeddingModel
        );
        JsonNode response = embeddingRestClient.post()
                .uri("/api/v1/embeddings")
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        List<Float> vector = new ArrayList<>();
        if (response != null) {
            response.path("data").path("embeddings").path(0)
                    .forEach(value -> vector.add(value.floatValue()));
        }
        return vector;
    }

    private String buildUserPrompt(String question, List<String> sources) {
        StringBuilder builder = new StringBuilder();
        builder.append("Question: ").append(question).append("\n\nContext:\n");
        if (sources.isEmpty()) {
            builder.append("- No context found.\n");
        } else {
            for (int i = 0; i < sources.size(); i++) {
                builder.append(i + 1).append(". ").append(sources.get(i)).append("\n");
            }
        }
        return builder.toString();
    }
}
