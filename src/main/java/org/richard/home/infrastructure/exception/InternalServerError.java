package org.richard.home.infrastructure.exception;

public class InternalServerError extends RuntimeException {
    public InternalServerError() {
        super("my internal server error!");
    }

    public InternalServerError(String message, Throwable cause) {
        super(message, cause);
    }
}
