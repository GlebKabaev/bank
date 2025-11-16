package com.example.bankcards.exception;

public class WrongCardOwnerException extends CardException {
    public WrongCardOwnerException(String message) {
        super(message);
    }
}
