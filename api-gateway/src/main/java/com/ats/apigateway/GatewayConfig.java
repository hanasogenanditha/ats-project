package com.ats.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("job-service", r -> r
                        .path("/jobs/**")
                        .uri("http://job-service:8084"))
                .route("application-service", r -> r
                        .path("/apply/**")
                        .uri("http://application-service:8081"))
                .route("screening-service", r -> r
                        .path("/screen/**")
                        .uri("http://screening-service:8082"))
                .route("notification-service", r -> r
                        .path("/notify/**")
                        .uri("http://notification-service:8083"))
                .build();
    }
}
//review
