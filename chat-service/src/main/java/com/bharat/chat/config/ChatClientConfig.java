package com.bharat.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ChatClientConfig {

    @Bean(name = "searchRestClient")
    public RestClient searchRestClient(@Value("${rag.search-service-url}") String searchServiceUrl) {
        return RestClient.builder().baseUrl(searchServiceUrl).build();
    }

    @Bean(name = "embeddingRestClient")
    public RestClient embeddingRestClient(@Value("${rag.embedding-service-url}") String embeddingServiceUrl) {
        return RestClient.builder().baseUrl(embeddingServiceUrl).build();
    }
}
