package com.bharat.chat.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "llm.provider", havingValue = "stub", matchIfMissing = true)
public class StubLlmProvider implements LlmProvider {

    @Override
    public String generate(String systemPrompt, String userPrompt) {
        return "Grounded answer based on retrieved context.\n\n"
                + userPrompt
                + "\n\n(System guidance: "
                + systemPrompt
                + ")";
    }
}
