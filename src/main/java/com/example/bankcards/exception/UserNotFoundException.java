package com.example.bankcards.exception;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) {
        super("User",message);
    }
}
