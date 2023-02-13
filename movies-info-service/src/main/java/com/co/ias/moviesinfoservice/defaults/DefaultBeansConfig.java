package com.co.ias.moviesinfoservice.defaults;

import com.co.ias.moviesinfoservice.repository.MovieInfoRepository;
import com.co.ias.moviesinfoservice.service.MoviesInfoServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultBeansConfig {

    @Bean
    public MoviesInfoServiceImpl moviesInfoService() {
        return new MoviesInfoServiceImpl();
    }

}
