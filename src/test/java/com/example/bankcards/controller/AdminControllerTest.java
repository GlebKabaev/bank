package com.example.bankcards.controller;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TicketDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AdminController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@Import(TestSecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void allCards_shouldReturnAllCards() throws Exception {
        List<CardDto> cards = List.of(new CardDto());
        when(cardService.findAllCards()).thenReturn(cards);

        mockMvc.perform(get("/admin/card")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(cardService, times(1)).findAllCards();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void allUserCards_shouldReturnUserCards() throws Exception {
        UUID userId = UUID.randomUUID();
        List<CardDto> cards = List.of(new CardDto());
        when(cardService.findCardsByUser(userId)).thenReturn(cards);

        mockMvc.perform(get("/admin/card/user/{userID}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(cardService, times(1)).findCardsByUser(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_shouldDeleteCard() throws Exception {
        UUID cardId = UUID.randomUUID();
        doNothing().when(cardService).deleteCard(cardId);

        mockMvc.perform(delete("/admin/card/{cardId}", cardId))
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).deleteCard(cardId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_notFound_shouldReturnBadRequest() throws Exception {
        UUID cardId = UUID.randomUUID();
        doThrow(new CardNotFoundException("Card not found")).when(cardService).deleteCard(cardId);

        mockMvc.perform(delete("/admin/card/{cardId}", cardId))
                .andExpect(status().isBadRequest());

        verify(cardService, times(1)).deleteCard(cardId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_shouldCreateCard() throws Exception {
        CreateCardRequest request = CreateCardRequest.builder()
                .number("4111111111111111")
                .owner("IVAN IVANOV")
                .expiryMonth(7)
                .expiryYear(2027)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .userId(UUID.randomUUID())
                .build();

        doNothing().when(cardService).createCard(any(CreateCardRequest.class));

        mockMvc.perform(post("/admin/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Карта успешно создана"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_invalidRequest_shouldReturnBadRequest() throws Exception {
        String invalidJson = "{}";

        mockMvc.perform(post("/admin/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(cardService, never()).createCard(any(CreateCardRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void blockCard_shouldBlockCard() throws Exception {
        UUID cardId = UUID.randomUUID();
        doNothing().when(cardService).blockCard(cardId);

        mockMvc.perform(patch("/admin/card/{cardId}/block", cardId))
                .andExpect(status().isOk())
                .andExpect(content().string("Карта успешно заблокирована"));

        verify(cardService, times(1)).blockCard(cardId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void blockCard_notFound_shouldReturnBadRequest() throws Exception {
        UUID cardId = UUID.randomUUID();
        doThrow(new CardNotFoundException("Card not found")).when(cardService).blockCard(cardId);

        mockMvc.perform(patch("/admin/card/{cardId}/block", cardId))
                .andExpect(status().isBadRequest());

        verify(cardService, times(1)).blockCard(cardId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void activateCard_shouldActivateCard() throws Exception {
        UUID cardId = UUID.randomUUID();
        doNothing().when(cardService).activateCard(cardId);

        mockMvc.perform(patch("/admin/card/{cardId}/activate", cardId))
                .andExpect(status().isOk())
                .andExpect(content().string("Карта успешно активирована"));

        verify(cardService, times(1)).activateCard(cardId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void activateCard_notFound_shouldReturnBadRequest() throws Exception {
        UUID cardId = UUID.randomUUID();
        doThrow(new CardNotFoundException("Card not found")).when(cardService).activateCard(cardId);

        mockMvc.perform(patch("/admin/card/{cardId}/activate", cardId))
                .andExpect(status().isBadRequest());

        verify(cardService, times(1)).activateCard(cardId);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void tickets_shouldReturnAllTickets() throws Exception {
        List<TicketDto> tickets = List.of(new TicketDto());
        when(ticketService.getAllTickets()).thenReturn(tickets);

        mockMvc.perform(get("/admin/ticket")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(ticketService, times(1)).getAllTickets();
    }

}
