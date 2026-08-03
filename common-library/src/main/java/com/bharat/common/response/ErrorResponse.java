package com.bharat.common.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {

    @Builder.Default
    private final boolean success = false;
    private String message;
    private int status;
    private String path;
    private String errorCode;
    private String correlationId;
    private List<String> errors;

    @Builder.Default
    private Instant timestamp = Instant.now();
}
