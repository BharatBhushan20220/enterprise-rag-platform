package com.bharat.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatAskRequest {

    @NotBlank
    private String sessionId;

    @NotBlank
    private String question;
}
