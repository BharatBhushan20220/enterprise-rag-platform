package com.bharat.common.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {

    private final boolean success=false;
    private String message;
    private int status;
    private String path;
    private List<String> errors;

    @Builder.Default
    private LocalDateTime timestamp=LocalDateTime.now();
}
