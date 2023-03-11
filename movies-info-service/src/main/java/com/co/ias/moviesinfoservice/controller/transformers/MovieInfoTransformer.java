package com.co.ias.moviesinfoservice.controller.transformers;

import com.co.ias.moviesinfoservice.controller.dto.MovieInfoDTO;
import com.co.ias.moviesinfoservice.domain.MovieInfo;
import org.mapstruct.Mapper;

@Mapper
public interface MovieInfoTransformer {

    MovieInfo toEntity(MovieInfoDTO movieInfoDTO);
}
