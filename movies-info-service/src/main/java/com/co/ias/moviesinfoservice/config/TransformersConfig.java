package com.co.ias.moviesinfoservice.config;

import com.co.ias.moviesinfoservice.controller.transformers.MovieInfoTransformer;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransformersConfig {

    @Bean
    public MovieInfoTransformer movieInfoTransformer(){
        return Mappers.getMapper(MovieInfoTransformer.class);
    }
}
