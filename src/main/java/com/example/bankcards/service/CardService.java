package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    @Transactional(readOnly = true)
    public List<CardDto> findAllCards() {
        return cardRepository.findAll().stream().map(this::toCardDto).toList();
    }
    @Transactional(readOnly = true)
    public List<CardDto> findCardsByUser(UUID id) {
        return cardRepository.findByUserId(id).stream().map(this::toCardDto).toList();
    }

    public CardDto toCardDto(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .maskedNumber(maskCardNumber(card.getNumber()))
                .owner(card.getOwner())
                .expiryMonth(card.getExpiryMonth())
                .expiryYear(card.getExpiryYear())
                .status(card.getStatus())
                .balance(card.getBalance())
                .build();
    }

    private static String maskCardNumber(String number) {
        if (number == null || number.length() < 4) {
            return "****";
        }
        int len = number.length();
        return "**** **** **** " + number.substring(len - 4);
    }
}
