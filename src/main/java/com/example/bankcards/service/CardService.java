package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardValidatorService cardValidatorService;
    private final UserValidatorService userValidatorService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CardDto> findAllCards() {
        return cardRepository.findAll().stream().map(this::toCardDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CardDto> findCardsByUser(UUID id) {
        userValidatorService.validateUserExistsById(id);
        return cardRepository.findByUserId(id).stream().map(this::toCardDto).toList();
    }

    @Transactional
    public void deleteCard(UUID id) {
        cardValidatorService.validateCardExistsById(id);
        cardRepository.deleteById(id);
    }

    @Transactional
    public void createCard(CreateCardRequest card) {
        cardValidatorService.ensureCardNotExistsByNumber(card.getNumber());
        Card cardEntity = toCardEntity(card);
        cardValidatorService.validateCardMatchWithUser(cardEntity);
        cardRepository.save(cardEntity);
    }

    @Transactional
    public void blockCard(UUID id) {
        cardValidatorService.validateCardExistsById(id);
        Card card = cardRepository.findById(id).get();
        card.setStatus(CardStatus.BLOCKED);
    }

    @Transactional
    public void activateCard(UUID id) {
        cardValidatorService.validateCardExistsById(id);
        Card card = cardRepository.findById(id).get();
        card.setStatus(CardStatus.ACTIVE);
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

    public Card toCardEntity(CreateCardRequest card) {
        userValidatorService.validateUserExistsById(card.getUserId());
        User user = userRepository.getReferenceById(card.getUserId());
        return Card.builder()
                .id(UUID.randomUUID())
                .number(card.getNumber())
                .owner(card.getOwner())
                .expiryMonth(card.getExpiryMonth())
                .expiryYear(card.getExpiryYear())
                .status(card.getStatus())
                .balance(card.getBalance())
                .user(user)
                .build();
    }

    private String maskCardNumber(String number) {
        int len = number.length();
        return "**** **** **** " + number.substring(len - 4);
    }
}
