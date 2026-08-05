package com.bharat.chat.controller;

import com.bharat.chat.dto.ChatAskRequest;
import com.bharat.chat.dto.ChatAskResponse;
import com.bharat.chat.service.ChatService;
import com.bharat.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "RAG chat ask and session history")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/ask")
    @Operation(summary = "Ask a RAG question")
    public ApiResponse<ChatAskResponse> ask(@Valid @RequestBody ChatAskRequest request) {
        log.info("Chat ask sessionId={}", request.getSessionId());
        return ApiResponse.ok(chatService.ask(request), "Answer generated");
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "Get chat history for a session")
    public ApiResponse<List<ChatAskResponse>> history(@PathVariable String sessionId) {
        return ApiResponse.ok(chatService.history(sessionId));
    }
}
