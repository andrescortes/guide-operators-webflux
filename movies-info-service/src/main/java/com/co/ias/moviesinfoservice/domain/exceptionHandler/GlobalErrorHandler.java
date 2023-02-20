package com.co.ias.moviesinfoservice.domain.exceptionHandler;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    private static List<String> getCollect(WebExchangeBindException exception) {
        return exception.getBindingResult().getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage).sorted()
            .collect(Collectors.toList());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<List<String>> handleRequestBodyError(WebExchangeBindException exception) {
        log.error("Exception Caught in handleRequestBodyError: {} ", exception.getMessage(),
            exception);
        List<String> stringList = getCollect(exception);
        log.error("Error is: {}", stringList);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(stringList);
    }
}
