package com.in28minutes.microservices.apigateway;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class ApiConfigurationGateway {


    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {

//        Function<PredicateSpec, Buildable<Route>> routeFunction= p-> p.path("/get")
//                .uri("http://httpbin.org:80");
        return builder.routes().route(p -> p
                .path("/get")
                .filters(f -> f
                        .addRequestHeader("MyHeader", "MyURI")
                        .addRequestParameter("Param", "MyValue"))
                .uri("http://httpbin.org:80"))
                .route(p -> p.path("/currency-exchange/**") // old url will not work
                        .uri("lb://currency-exchange")) //lb= load balancing, go to eureka, find location of this service and lb
                .route(p -> p.path("/currency-conversion/**")
                        .uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion-feign/**")
                        .uri("lb://currency-conversion"))
                .route(p -> p.path("/currency-conversion-new/**")
                        .filters(f -> f.rewritePath(
                                "/currency-conversion-new/(?<segment>.*)",
                                "/currency-conversion-feign/${segment}"))
                        .uri("lb://currency-conversion"))
                .build();
    }
}
