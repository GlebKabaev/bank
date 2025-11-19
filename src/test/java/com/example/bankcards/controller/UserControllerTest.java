package com.example.bankcards.controller;

import com.example.bankcards.TestSecurityConfig;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.MoneyTransferDto;
import com.example.bankcards.dto.TicketDto;
import com.example.bankcards.entity.CardStatus;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.example.bankcards.security.JwtAuthenticationFilter.class
        )
)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardService cardService;

    @MockitoBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final UUID USER_CARD_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Test
    @WithMockUser(roles = "USER")
    void getCards_shouldReturnUserCardsByStatusAndPage() throws Exception {
        List<CardDto> cards = List.of(
                CardDto.builder()
                        .id(USER_CARD_ID)
                        .maskedNumber("************1111")
                        .owner("IVAN IVANOV")
                        .status(CardStatus.ACTIVE)
                        .balance(BigDecimal.valueOf(1000.50))
                        .build()
        );

        when(cardService.getUsersCard(0, CardStatus.ACTIVE)).thenReturn(cards);

        mockMvc.perform(get("/card/{status}/{page}", "ACTIVE", 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(USER_CARD_ID.toString()))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].balance").value(1000.50));

        verify(cardService, times(1)).getUsersCard(0, CardStatus.ACTIVE);
    }

    @Test
    @WithMockUser(roles = "USER")
    void transfer_shouldPerformMoneyTransferAndReturnSuccessMessage() throws Exception {
        MoneyTransferDto transferDto = MoneyTransferDto.builder()
                .fromCardId(USER_CARD_ID)
                .toCardId(UUID.randomUUID())
                .amount(BigDecimal.valueOf(500.00))
                .build();

        doNothing().when(cardService).moneyTransfer(any(MoneyTransferDto.class));

        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Перевод произведен успешно"));

        verify(cardService, times(1)).moneyTransfer(transferDto);
    }



    @Test
    @WithMockUser(roles = "USER")
    void createTicket_shouldCreateSupportTicketAndReturnSuccessMessage() throws Exception {
        TicketDto ticketDto = TicketDto.builder()
                .cardId(USER_CARD_ID)
                .build();

        doNothing().when(ticketService).createTicket(any(TicketDto.class));

        mockMvc.perform(post("/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Заявка успешно создана"));

        verify(ticketService, times(1)).createTicket(ticketDto);
    }


    @Test
    @WithMockUser(roles = "USER")
    void getCards_withBlockedStatus_shouldWork() throws Exception {
        when(cardService.getUsersCard(1, CardStatus.BLOCKED)).thenReturn(List.of());

        mockMvc.perform(get("/card/BLOCKED/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(cardService).getUsersCard(1, CardStatus.BLOCKED);
    }
}