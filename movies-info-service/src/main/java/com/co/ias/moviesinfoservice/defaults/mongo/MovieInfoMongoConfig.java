package com.co.ias.moviesinfoservice.defaults.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.co.ias.moviesinfoservice.repository", reactiveMongoTemplateRef = "mongoTemplateMovieInfo")
public class MovieInfoMongoConfig {

}
