package com.bharat.chat.service;

import com.bharat.chat.dto.ChatAskRequest;
import com.bharat.chat.dto.ChatAskResponse;
import com.bharat.chat.entity.ChatMessage;
import com.bharat.chat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final RestClient searchRestClient;

    public ChatAskResponse ask(ChatAskRequest request) {
        List<String> sources = retrieveSources(request.getQuestion());
        String answer = buildAnswer(request.getQuestion(), sources);

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
            JsonNode response = searchRestClient.post()
                    .uri("/api/v1/search")
                    .body(Map.of("query", question, "topK", 3))
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

    private String buildAnswer(String question, List<String> sources) {
        if (sources.isEmpty()) {
            return "I could not find relevant knowledge-base context for: \"" + question
                    + "\". Index documents first, then retry.";
        }
        String context = sources.stream().limit(3).collect(Collectors.joining(" | "));
        return "Based on retrieved context: " + context + ". Question answered: " + question;
    }
}
