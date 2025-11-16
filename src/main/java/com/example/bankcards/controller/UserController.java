package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.MoneyTransferDto;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @GetMapping("/card")
    public List<CardDto> getCards() {
        return cardService.getUsersCard();
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody MoneyTransferDto moneyTransferDto) {
        cardService.moneyTransfer(moneyTransferDto);
        return ResponseEntity.ok("Перевод произведен успешно");
    }

}
