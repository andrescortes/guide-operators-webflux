package com.co.ias.moviesinfoservice.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder(toBuilder = true)
public class MovieInfoDTO {


    private String movieInfoId;

    private String name;

    private Integer year;

    private List<String> cast;

    private LocalDateTime releaseDate;
}
