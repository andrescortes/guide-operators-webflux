package com.reactivespring.globalerrohandler;

import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

    @ExceptionHandler(MoviesInfoClientException.class)
    public ResponseEntity<String> handleClientException(MoviesInfoClientException exception) {
        log.error("Exception caught in handleClientException: {}", exception.getMessage(),
            exception);
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
    }
    @ExceptionHandler(MoviesInfoServerException.class)
    public ResponseEntity<String> handleServerException(MoviesInfoServerException exception) {
        log.error("Exception caught in handleServerException: {}", exception.getMessage(),
            exception);
        return ResponseEntity.status(exception.getStatusCode()).body(exception.getMessage());
    }
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Map<String,String>> handleServerException(WebClientRequestException exception) {
        log.error("Exception caught in handleServerException: {}", exception.getMessage(),
            exception);
        Map<String,String> errors = new HashMap<>();
        errors.put("message", exception.getMessage());
        errors.put("localizedMessage", exception.getLocalizedMessage());
        errors.put("method", exception.getMethod().name());
        errors.put("host", exception.getUri().getHost());
        errors.put("mostSpecificCause", exception.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errors);
    }
}
