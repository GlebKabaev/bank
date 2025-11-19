package com.example.bankcards.exception;

public class ServerException extends RuntimeException {

    public ServerException() {
        super("Server error");
    }
}