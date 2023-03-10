package com.co.ias.moviesinfoservice.domain;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Document
@Builder(toBuilder = true)
public class MovieInfo {

    @Id
    private String movieInfoId;
    @NotBlank(message = "movieInfo.name must be present")
    private String name;
    @NotNull
    @Positive(message = "movieInfo.year element must be a strictly positive number >= 0")
    private Integer year;

    private List<@NotBlank(message = "movieInfo.cast must be present") String> cast;

    private LocalDateTime releaseDate;
}
