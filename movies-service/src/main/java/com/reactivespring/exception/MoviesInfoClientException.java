package com.reactivespring.exception;

public class MoviesInfoClientException extends RuntimeException {

    private final String message;
    private final int statusCode;

    public MoviesInfoClientException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode=statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }


    public int getStatusCode() {
        return statusCode;
    }

}
