package org.example.springcloudapigatewaytokenbucket;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    RateLimiterGatewayFilterFactory rateLimiterGatewayFilterFactory = new RateLimiterGatewayFilterFactory(5, 10);

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("register-legalservice", r -> r.path("/register-legalservice/hello")
                        .filters(
                                f -> f.filter(
                                        rateLimiterGatewayFilterFactory.apply(new RateLimiterConfig())
                                )
                        )
                        .uri("http://localhost:8070/register-legalservice/hello")
                )
                .build();
    }
}