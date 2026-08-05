package com.bharat.common.exception;

import com.bharat.common.constants.AppConstants;
import com.bharat.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Order
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException exception,
            HttpServletRequest request) {
        log.warn(
                "Handled {} on {}: {}",
                exception.getClass().getSimpleName(),
                request.getRequestURI(),
                exception.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .message(exception.getMessage())
                .status(exception.getStatusCode())
                .path(request.getRequestURI())
                .errors(List.of(exception.getMessage()))
                .correlationId(correlationId(request))
                .build();

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.warn("Validation failed on {}: {}", request.getRequestURI(), errors);

        ErrorResponse response = ErrorResponse.builder()
                .message("Validation failed")
                .status(400)
                .path(request.getRequestURI())
                .errors(errors)
                .correlationId(correlationId(request))
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception exception,
            HttpServletRequest request) {
        log.error("Unhandled error on {}", request.getRequestURI(), exception);

        ErrorResponse response = ErrorResponse.builder()
                .message("Internal server error")
                .status(500)
                .path(request.getRequestURI())
                .errors(List.of(exception.getMessage() != null ? exception.getMessage() : "Unexpected error"))
                .correlationId(correlationId(request))
                .build();

        return ResponseEntity.internalServerError().body(response);
    }

    private String correlationId(HttpServletRequest request) {
        String fromMdc = MDC.get(AppConstants.CORRELATION_ID_MDC_KEY);
        if (fromMdc != null && !fromMdc.isBlank()) {
            return fromMdc;
        }
        return request.getHeader(AppConstants.CORRELATION_ID_HEADER);
    }
}
