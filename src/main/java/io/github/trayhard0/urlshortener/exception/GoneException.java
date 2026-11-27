package io.github.trayhard0.urlshortener.exception;

public class GoneException extends RuntimeException {
    public GoneException(String message) {
        super(message);
    }
}
