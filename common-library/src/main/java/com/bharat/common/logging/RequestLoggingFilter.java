package com.bharat.common.logging;

import com.bharat.common.constants.AppConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Access-style request logging (method, path, status, duration). Bodies are not logged.
 */
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/api-docs")
                || path.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            filterChain.doFilter(request, response);
        } finally {
            watch.stop();
            log.info(
                    "HTTP {} {} -> {} ({} ms) correlationId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    watch.getTotalTimeMillis(),
                    request.getHeader(AppConstants.CORRELATION_ID_HEADER)
            );
        }
    }
}
