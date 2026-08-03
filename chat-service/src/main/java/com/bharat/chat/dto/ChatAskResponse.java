package com.bharat.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ChatAskResponse {

    private UUID messageId;
    private String sessionId;
    private String question;
    private String answer;
    private List<String> sources;
}
