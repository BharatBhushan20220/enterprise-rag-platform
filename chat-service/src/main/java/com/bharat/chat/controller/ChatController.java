package com.bharat.chat.controller;

import com.bharat.chat.dto.ChatAskRequest;
import com.bharat.chat.dto.ChatAskResponse;
import com.bharat.chat.service.ChatService;
import com.bharat.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/ask")
    public ApiResponse<ChatAskResponse> ask(@Valid @RequestBody ChatAskRequest request) {
        return ApiResponse.ok(chatService.ask(request), "Answer generated");
    }

    @GetMapping("/sessions/{sessionId}")
    public ApiResponse<List<ChatAskResponse>> history(@PathVariable String sessionId) {
        return ApiResponse.ok(chatService.history(sessionId));
    }
}
