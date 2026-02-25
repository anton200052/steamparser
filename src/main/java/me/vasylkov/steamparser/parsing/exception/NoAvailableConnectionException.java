package me.vasylkov.steamparser.parsing.exception;

public class NoAvailableConnectionException extends RuntimeException {
    public NoAvailableConnectionException(String message) {
        super(message);
    }
}
