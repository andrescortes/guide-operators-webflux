package com.co.ias.moviesinfoservice.defaults.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

/**
 * @see <a href="https://github.com/visweshwar/multi-mongo-reactive"> reactive-mongo-config...</a>
 */
//@Configuration
@RequiredArgsConstructor
public class MongoConfiguration {

    private final MovieInfoConfigProperties movieInfoConfigProperties;

    @Primary
    @Bean
    public MongoClient reactiveMongoClientMovieInfo() {
        return MongoClients.create(
            createMongoClientSettings(movieInfoConfigProperties.getMovies()));
    }

    @Primary
    @Bean("mongoTemplateMovieInfo")
    public ReactiveMongoTemplate reactiveMongoTemplateMovieInfo() {
        return new ReactiveMongoTemplate(reactiveMongoClientMovieInfo(),
            movieInfoConfigProperties.getMovies().getDatabase());
    }

    private MongoClientSettings createMongoClientSettings(MongoProperties mongoProperties) {
        ConnectionString connectionString = new ConnectionString(mongoProperties.getUri());
        return MongoClientSettings.builder()
            .readConcern(ReadConcern.DEFAULT)
            .writeConcern(WriteConcern.MAJORITY)
            .readPreference(ReadPreference.primary())
            .applyConnectionString(connectionString)
            .build();
    }
}
