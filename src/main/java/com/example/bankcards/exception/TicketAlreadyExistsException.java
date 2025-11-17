package com.example.bankcards.exception;

public class TicketAlreadyExistsException extends TicketException {
    public TicketAlreadyExistsException(String message) {
        super(message);
    }
}
