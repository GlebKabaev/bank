package com.example.bankcards.service;

import com.example.bankcards.exception.TicketAlreadyExistsException;
import com.example.bankcards.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TicketValidatorService {
    private final TicketRepository ticketRepository;
    private final String ticketAlreadyExistsExceptionMessage;

    public TicketValidatorService(TicketRepository ticketRepository,
                                  @Value("${app.ticket.already-exists-by.cardId}") String ticketAlreadyExistsExceptionMessage) {
        this.ticketRepository = ticketRepository;
        this.ticketAlreadyExistsExceptionMessage = ticketAlreadyExistsExceptionMessage;
    }

    public void validateTicketExistByCardId(UUID cardId){
        if(ticketRepository.existsByCard_Id(cardId)){
            throw new TicketAlreadyExistsException(ticketAlreadyExistsExceptionMessage);
        }
    }
}
