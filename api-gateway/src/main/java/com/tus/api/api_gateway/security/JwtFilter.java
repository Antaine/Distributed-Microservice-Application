package com.tus.api.api_gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // public endpoints
        if (path.startsWith("/login")) {
            return chain.filter(exchange);
        }

        if (HttpMethod.POST.equals(method) && "/api/users".equals(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("❌ Missing or invalid Authorization header for {} {}", method, path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtUtil.validateToken(token);
            String role = jwtUtil.extractRole(token);

            logger.info("➡️ {} {} by {}", method, path, username);
            logger.info("User: {}, Role: {}", username, role);

            boolean adminOnly =
                    (HttpMethod.GET.equals(method) && "/api/users".equals(path)) ||
                    HttpMethod.DELETE.equals(method) ||
                    HttpMethod.PUT.equals(method);

            if (adminOnly && !"ADMIN".equals(role)) {
                logger.warn("⛔ Forbidden: {} tried ADMIN operation", username);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);

        } catch (Exception e) {
            logger.error("❌ Invalid token: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}