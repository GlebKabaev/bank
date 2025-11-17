package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.MoneyTransferDto;
import com.example.bankcards.dto.TicketDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Ticket;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TicketService;
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
    private final TicketService ticketService;
    @GetMapping("/card/{status}/{page}")
    public List<CardDto> getCards(@PathVariable("page") int page, @PathVariable("status") CardStatus cardStatus) {
        return cardService.getUsersCard(page, cardStatus);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody MoneyTransferDto moneyTransferDto) {
        cardService.moneyTransfer(moneyTransferDto);
        return ResponseEntity.ok("Перевод произведен успешно");
    }
    @PostMapping("/ticket")
    public ResponseEntity<String> createTicket(@RequestBody TicketDto ticket){

         ticketService.createTicket(ticket);
         return ResponseEntity.ok("Заявка успешно создана");
    }

}
