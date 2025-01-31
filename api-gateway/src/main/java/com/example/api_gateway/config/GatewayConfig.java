package com.example.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouter(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route("userservice", r -> r.path("/auth/**").uri("lb://userservice"))
                .route("userservice", r -> r.path("/users/{userId}")
                        .uri("lb://userservice"))
                .route("userservice", r -> r.path("/users/existUser/**")
                        .uri("lb://userservice"))

                .route("userservice", r -> r.path("/users/**").
                        filters(filter -> filter.filter(jwtAuthenticationFilter))
                        .uri("lb://userservice"))
                .route("productservice", r -> r.path("/products/existStock")
                        .uri("lb://productservice"))

                .route("productservice", r -> r.path("/products/order/all").
                        filters(filter -> filter.filter(jwtAuthenticationFilter))
                        .uri("lb://productservice"))

                .route("productservice", r -> r.path("/products/**").
                        filters(filter -> filter.filter(jwtAuthenticationFilter))
                        .uri("lb://productservice"))
                .route("orderservice", r -> r.path("/orders/**").
                        filters(filter -> filter.filter(jwtAuthenticationFilter))
                        .uri("lb://orderservice"))
                .route("emailservice", r -> r.path("/emails/**").uri("lb://emailservice"))
                .build();
    }
}
