package com.example.backjeon_BE.exception;

public class InvalidGameIdException extends RuntimeException {
    public InvalidGameIdException(String gameId) {
        super("Invalid game ID format: " + gameId);
    }
}

