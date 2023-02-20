package com.reactivespring.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReviewRouter {

    private final String URL = "/v1/reviews";

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return route()
            .GET("/v1/helloworld", (request -> ServerResponse.ok().bodyValue("HelloWorld")))
            .POST(URL, request -> reviewHandler.addReview(request))
            .build();
    }
}
