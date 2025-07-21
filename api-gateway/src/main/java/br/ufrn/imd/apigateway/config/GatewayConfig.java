package br.ufrn.imd.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("microservice-mcp", r -> r.path("/api/mcp/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .circuitBreaker(config -> config
                                        .setName("mcpCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/mcp"))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setBackoff(java.time.Duration.ofMillis(100), java.time.Duration.ofMillis(1000), 2, false)))
                        .uri("lb://microservice-mcp"))
                .route("microservice-ai", r -> r.path("/api/ai/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .circuitBreaker(config -> config
                                        .setName("aiCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/ai"))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setBackoff(java.time.Duration.ofMillis(100), java.time.Duration.ofMillis(1000), 2, false)))
                        .uri("lb://microservice-ai"))
                .route("microservice-serverless", r -> r.path("/api/serverless/**")
                        .filters(f -> f
                                .stripPrefix(2)
                                .circuitBreaker(config -> config
                                        .setName("serverlessCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/serverless"))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setBackoff(java.time.Duration.ofMillis(100), java.time.Duration.ofMillis(1000), 2, false)))
                        .uri("lb://microservice-serverless"))
                .build();
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just("1");
    }
}
