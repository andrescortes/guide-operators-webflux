package com.reactivespring.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReviewRouter {

    private static final String URL_REVIEWS = "/v1/reviews";

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return route()
            .nest(path(URL_REVIEWS), builder -> {
                builder
                    .POST("", request -> reviewHandler.addReview(request))
                    .GET("", request -> reviewHandler.getReviews(request))
                    .PUT("/{id}", request -> reviewHandler.updateReview2(request))
                    .DELETE("/{id}", request -> reviewHandler.deleteReview(request))
                ;

            })
            .GET("/v1/helloworld", (request -> ServerResponse.ok().bodyValue("HelloWorld")))
            /*.POST(URL, request -> reviewHandler.addReview(request))
            .GET(URL, request -> reviewHandler.getReviews(request))*/
            .build();
    }
}
