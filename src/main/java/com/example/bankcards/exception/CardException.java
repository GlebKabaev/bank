package com.example.bankcards.exception;

public class CardException extends BusinessException {
    public CardException(String message) {
        super("card", message);
    }
}
