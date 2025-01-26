package com.example.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouter(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route("userservice", r -> r.path("/users/**").uri("lb://userservice"))
                .route("productservice", r -> r.path("/products/**").uri("lb://productservice"))
                .route("orderservice", r -> r.path("/orders/**").uri("lb://orderservice"))
                .build();
    }
}
