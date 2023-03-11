package com.reactivespring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
public class MoviesInfoServerException extends RuntimeException {

    private String message;
    private Integer statusCode;

}
