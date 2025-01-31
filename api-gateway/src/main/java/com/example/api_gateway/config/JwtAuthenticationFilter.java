package com.example.api_gateway.config;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    //    @Autowired
    private JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!exchange.getRequest().getPath().toString().startsWith("/auth")) {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    if (jwtUtils.isTokenNotExpired(token)) {
                        Claims claims = jwtUtils.parseClaims(token);
//                        supongo que aqui realiza lo que tiene que hacer para cargar al filtroy que tenga lo poderes que se le quera conceder
//                        exchange.getRequest().mutate().header("username", claims.getSubject()).build();
                    } else {
                        return onErrorJWT(exchange, "Invalid JWT Token");
                    }
                } catch (Exception e) {
                    return onErrorJWT(exchange, "JWT Token validation failed");
                }
            } else {
                return onError(exchange, "Authorization header is missing or invalid");
            }
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onErrorJWT(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String responseBody = "{ \"error\": \"Unauthorized\", \"message\": \"" + message + "\" }";
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(responseBody.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(err.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private List<GrantedAuthority> extractAuthorities(Claims claims) {
        // Extract and return authorities from claims
        return new ArrayList<>();
    }
}