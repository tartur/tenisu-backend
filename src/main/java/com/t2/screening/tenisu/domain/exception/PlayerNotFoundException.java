package com.t2.screening.tenisu.domain.exception;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException() {
        this("Player not found");
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }
}
