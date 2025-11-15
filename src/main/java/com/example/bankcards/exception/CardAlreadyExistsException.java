package com.example.bankcards.exception;

public class CardAlreadyExistsException extends CardException {
    public CardAlreadyExistsException(String message) {
        super(message);
    }
}
