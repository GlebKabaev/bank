package com.example.bankcards.exception;

public class UserException extends BusinessException {
    public UserException(String message) {
        super("User",message);
    }
}
