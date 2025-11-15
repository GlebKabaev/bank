package com.example.bankcards.controller;


import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
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
    private final CardService cardService;

    @GetMapping("/card")
    public ResponseEntity<List<CardDto>> getAllCards() {
        return ResponseEntity.ok(cardService.findAllCards());
    }

    @GetMapping("/card/user/{userID}")
    public ResponseEntity<List<CardDto>> getAllUserCards(@PathVariable("userID") UUID userID) {
        return ResponseEntity.ok(cardService.findCardsByUser(userID));
    }
    @DeleteMapping("/card/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/card")
    public ResponseEntity<String> createCard(@RequestBody @Valid CreateCardRequest card) {
        cardService.createCard(card);
        return ResponseEntity.ok("Карта успешно создана");
    }
    @PatchMapping("/card/{cardId}/block")
    public ResponseEntity<String> blockCard(@PathVariable UUID cardId) {
        cardService.blockCard(cardId);
        return ResponseEntity.ok("Карта успешно заблокирована");
    }
    @PatchMapping("/card/{cardId}/activate")
    public ResponseEntity<String> activateCard(@PathVariable UUID cardId) {
        cardService.activateCard(cardId);
        return ResponseEntity.ok("Карта успешно активирована");
    }
}
