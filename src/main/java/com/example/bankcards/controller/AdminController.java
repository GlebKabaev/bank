package com.example.bankcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.TicketDto;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TicketService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Администратор")
public class AdminController {
    private final TicketService ticketService;
    private final CardService cardService;

    @GetMapping("/card")
    @Operation(summary = "Получение всех банковских карт")
    public ResponseEntity<List<CardDto>> allCards() {
        return ResponseEntity.ok(cardService.findAllCards());
    }

    @GetMapping("/card/user/{userID}")
    @Operation(summary = "Получение всех карт конкретного пользователя")
    public ResponseEntity<List<CardDto>> allUserCards(
            @Parameter(description = "ID пользователя")
            @PathVariable("userID") UUID userID) {

        return ResponseEntity.ok(cardService.findCardsByUser(userID));
    }

    @DeleteMapping("/card/{cardId}")
    @Operation(summary = "Удаление карты по ее ID")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "ID карты")
            @PathVariable UUID cardId) {

        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/card")
    @Operation(summary = "Создание новой банковской карты")
    public ResponseEntity<String> createCard(
            @Parameter(description = "Данные для создания карты")
            @RequestBody @Valid CreateCardRequest card) {

        cardService.createCard(card);
        return ResponseEntity.ok("Карта успешно создана");
    }

    @PatchMapping("/card/{cardId}/block")
    @Operation(summary = "Блокировка карты по её ID")
    public ResponseEntity<String> blockCard(
            @Parameter(description = "ID карты")
            @PathVariable UUID cardId) {

        cardService.blockCard(cardId);
        return ResponseEntity.ok("Карта успешно заблокирована");
    }

    @PatchMapping("/card/{cardId}/activate")
    @Operation(summary = "Активация карты по её ID")
    public ResponseEntity<String> activateCard(
            @Parameter(description = "ID карты")
            @PathVariable UUID cardId) {

        cardService.activateCard(cardId);
        return ResponseEntity.ok("Карта успешно активирована");
    }

    @GetMapping("/ticket")
    @Operation(summary = "Получение всех тикетов поддержки")
    public ResponseEntity<List<TicketDto>> tickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }
}
