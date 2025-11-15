package com.example.bankcards.exception;

public class CardNotFoundException extends CardException {
    public CardNotFoundException(String message) {
        super(message);
    }
}
