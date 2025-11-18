package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.MoneyTransferDto;
import com.example.bankcards.dto.TicketDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
@Tag(name = "Пользователь")
public class UserController {

    private final CardService cardService;
    private final TicketService ticketService;

    @GetMapping("/card/{status}/{page}")
    @Operation(summary = "Получение карт пользователя по статусу и странице")
    public List<CardDto> getCards(
            @Parameter(description = "Номер страницы") @PathVariable("page") int page,
            @Parameter(description = "Статус карты") @PathVariable("status") CardStatus cardStatus) {

        return cardService.getUsersCard(page, cardStatus);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Перевод денежных средств между картами пользователя")
    public ResponseEntity<String> transfer(
            @Parameter(description = "Данные для перевода средств")
            @RequestBody MoneyTransferDto moneyTransferDto) {

        cardService.moneyTransfer(moneyTransferDto);
        return ResponseEntity.ok("Перевод произведен успешно");
    }

    @PostMapping("/ticket")
    @Operation(summary = "Создание тикета в службу поддержки для блокировки")
    public ResponseEntity<String> createTicket(
            @Parameter(description = "Данные тикета")
            @RequestBody TicketDto ticket) {

        ticketService.createTicket(ticket);
        return ResponseEntity.ok("Заявка успешно создана");
    }
}
