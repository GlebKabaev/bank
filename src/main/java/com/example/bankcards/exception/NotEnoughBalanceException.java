package com.example.bankcards.exception;

public class NotEnoughBalanceException extends CardException {
    public NotEnoughBalanceException(String message) {
        super(message);
    }
}
