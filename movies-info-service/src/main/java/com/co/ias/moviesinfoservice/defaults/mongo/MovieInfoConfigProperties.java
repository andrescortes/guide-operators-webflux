package com.co.ias.moviesinfoservice.defaults.mongo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Log4j2
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MovieInfoConfigProperties {

    private MongoProperties movies;

}


