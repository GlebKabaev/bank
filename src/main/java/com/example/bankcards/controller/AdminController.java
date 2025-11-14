package com.example.bankcards.controller;


import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    public List<CardDto> getAllCards() {
        return cardService.findAllCards();
    }

    @GetMapping("/card/user/{userID}")
    public List<CardDto> getAllUserCards(@PathVariable("userID") UUID userID) {
        return cardService.findCardsByUser(userID);
    }
}
