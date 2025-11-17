package com.example.bankcards.exception;

public class TicketException extends BusinessException {
    public TicketException(String message) {
        super("Ticket",message);
    }
}
