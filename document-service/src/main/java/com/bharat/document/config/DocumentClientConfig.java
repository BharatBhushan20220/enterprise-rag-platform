package com.bharat.document.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class DocumentClientConfig {

    @Bean(name = "embeddingRestClient")
    public RestClient embeddingRestClient(@Value("${rag.embedding-service-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean(name = "searchRestClient")
    public RestClient searchRestClient(@Value("${rag.search-service-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
