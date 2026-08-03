package com.bharat.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ChatClientConfig {

    @Bean
    public RestClient searchRestClient(@Value("${rag.search-service-url}") String searchServiceUrl) {
        return RestClient.builder()
                .baseUrl(searchServiceUrl)
                .build();
    }
}
