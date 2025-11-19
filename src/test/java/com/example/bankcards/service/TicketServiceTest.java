package com.example.bankcards.service;

import com.example.bankcards.dto.TicketDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Ticket;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private CardService cardService;

    @Mock
    private CardValidatorService cardValidatorService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketValidatorService ticketValidatorService;

    @InjectMocks
    private TicketService ticketService;

    private UUID cardId;
    private UUID userId;
    private UUID ticketId;
    private Card card;
    private User user;
    private Ticket ticket;
    private TicketDto ticketDto;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        userId = UUID.randomUUID();
        ticketId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        card = Card.builder()
                .id(cardId)
                .status(CardStatus.ACTIVE)
                .user(user)
                .build();

        ticket = Ticket.builder()
                .id(ticketId)
                .user(user)
                .card(card)
                .build();

        ticketDto = new TicketDto(cardId);
    }

    @Test
    void createTicket_shouldCreateAndSaveTicket() {
        doNothing().when(ticketValidatorService).validateTicketExistByCardId(cardId);
        when(cardService.findCardById(cardId)).thenReturn(card);
        doNothing().when(cardValidatorService).validateCardMatchWithUser(card);
        doNothing().when(cardValidatorService).validateCardStatus(card, CardStatus.ACTIVE);
        when(userService.getCurrentUser()).thenReturn(user);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        ticketService.createTicket(ticketDto);

        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void getAllTickets_shouldReturnListOfTicketDto() {
        List<Ticket> tickets = List.of(ticket);
        when(ticketRepository.findAll()).thenReturn(tickets);

        List<TicketDto> result = ticketService.getAllTickets();

        assertEquals(1, result.size());
        assertEquals(cardId, result.get(0).getCardId());
    }
}