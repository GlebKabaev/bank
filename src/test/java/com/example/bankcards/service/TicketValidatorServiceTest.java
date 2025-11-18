package com.example.bankcards.service;

import com.example.bankcards.exception.TicketAlreadyExistsException;
import com.example.bankcards.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketValidatorServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketValidatorService ticketValidatorService;

    private String exceptionMessage = "Ticket already exists for this card";

    @BeforeEach
    void setUp() {
        ticketValidatorService = new TicketValidatorService(ticketRepository, exceptionMessage);
    }

    @Test
    void validateTicketExistByCardId_whenTicketDoesNotExist_shouldNotThrowException() {
        UUID cardId = UUID.randomUUID();
        when(ticketRepository.existsByCard_Id(cardId)).thenReturn(false);

        assertDoesNotThrow(() -> ticketValidatorService.validateTicketExistByCardId(cardId));
    }

    @Test
    void validateTicketExistByCardId_whenTicketExists_shouldThrowTicketAlreadyExistsException() {
        UUID cardId = UUID.randomUUID();
        when(ticketRepository.existsByCard_Id(cardId)).thenReturn(true);

        assertThrows(TicketAlreadyExistsException.class,
                () -> ticketValidatorService.validateTicketExistByCardId(cardId));
    }
}