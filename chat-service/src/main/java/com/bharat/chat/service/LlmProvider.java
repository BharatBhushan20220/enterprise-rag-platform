package com.bharat.chat.service;

public interface LlmProvider {

    String generate(String systemPrompt, String userPrompt);
}
