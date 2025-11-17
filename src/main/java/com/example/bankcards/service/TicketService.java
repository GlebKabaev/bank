package com.example.bankcards.service;

import com.example.bankcards.dto.TicketDto;
import com.example.bankcards.entity.Ticket;
import com.example.bankcards.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final UserService userService;
    private final CardService cardService;
    private final TicketRepository ticketRepository;

    @Transactional
    public void createTicket(TicketDto ticketDto){
//Todo: validate???
        ticketRepository.save(convertToEntity(ticketDto));


    }
    private Ticket convertToEntity(TicketDto ticketDto){
        return Ticket.builder()
                .user(userService.getCurrentUser())
                .card(cardService.findCardById(ticketDto.getCardId()))
                .id(UUID.randomUUID())
                .build();
    }
}
