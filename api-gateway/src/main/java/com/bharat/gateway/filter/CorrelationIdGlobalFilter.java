package com.bharat.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Propagates / creates X-Correlation-Id for gateway requests and logs access.
 */
@Component
@Slf4j
public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        String finalCorrelationId = correlationId;
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(CORRELATION_ID_HEADER, finalCorrelationId)
                .build();
        exchange.getResponse().getHeaders().set(CORRELATION_ID_HEADER, finalCorrelationId);

        long started = System.currentTimeMillis();
        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .doFinally(signal -> {
                    String path = exchange.getRequest().getURI().getPath();
                    if (path.startsWith("/actuator")
                            || path.startsWith("/swagger-ui")
                            || path.startsWith("/v3/api-docs")
                            || path.startsWith("/webjars")
                            || path.startsWith("/docs/")) {
                        return;
                    }
                    Integer status = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : null;
                    log.info(
                            "GW {} {} -> {} ({} ms) correlationId={}",
                            exchange.getRequest().getMethod(),
                            path,
                            status,
                            System.currentTimeMillis() - started,
                            finalCorrelationId);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
