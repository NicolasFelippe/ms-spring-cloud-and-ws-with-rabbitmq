package com.rabbitmq.servicegateway.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BeanConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("serviceTest", r -> r.path("/service-test/**")
                        .filters(f -> f.circuitBreaker(c -> c.setName("serviceTest")
                                       .setFallbackUri("forward:/fallback/first")))
                        .uri("lb://SERVICE-TEST/"))

                .route("serviceWebsocket",r -> r.path("/websocket/**")
                        .uri("lb://SERVICE-WEBSOCKET/"))
                .route("serviceWebsocketAPI",r -> r.path("/api/**")
                        .uri("lb://SERVICE-WEBSOCKET/"))
                .build();
    }


    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(2)).build())
                .build());
    }

}