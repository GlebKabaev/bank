package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TicketDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Ticket;
import com.example.bankcards.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final UserService userService;
    private final CardService cardService;
    private final CardValidatorService cardValidatorService;
    private final TicketRepository ticketRepository;
    private final TicketValidatorService ticketValidatorService;

    @Transactional
    public void createTicket(TicketDto ticketDto) {
        ticketValidatorService.validateTicketExistByCardId(ticketDto.getCardId());
        Card cardEntity = cardService.findCardById(ticketDto.getCardId());
        cardValidatorService.validateCardMatchWithUser(cardEntity);
        cardValidatorService.validateCardStatus(cardEntity, CardStatus.ACTIVE);
        ticketRepository.save(convertToEntity(ticketDto));
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getAllTickets() {
        return ticketRepository.findAll().stream().map(this::toTicketDto).toList();
    }

    private Ticket convertToEntity(TicketDto ticketDto) {
        return Ticket.builder()
                .user(userService.getCurrentUser())
                .card(cardService.findCardById(ticketDto.getCardId()))
                .id(UUID.randomUUID())
                .build();
    }

    private TicketDto toTicketDto(Ticket ticket) {
        return TicketDto.builder()
                .cardId(ticket.getCard().getId())
                .build();
    }
}
