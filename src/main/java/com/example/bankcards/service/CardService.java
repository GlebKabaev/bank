package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.MoneyTransferDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardValidatorService cardValidatorService;
    private final UserValidatorService userValidatorService;
    private final UserRepository userRepository;
    private final UserService userService;

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
        cardValidatorService.validateCardCreate(card);
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

    public List<CardDto> getUsersCard(int page, CardStatus status) {
        final int size = 2;
        List<Card> allCards = userService.getCurrentUser().getCards();
        List<Card> filteredCards = (status == null)
                ? allCards
                : allCards.stream().filter(card -> card.getStatus() == status).toList();
        int from = (page - 1) * size;
        if (from >= filteredCards.size() || from < 0) {
            return Collections.emptyList();
        }
        int to = Math.min(from + size, filteredCards.size());
        return filteredCards.subList(from, to).stream().map(this::toCardDto).toList();
    }

    @Transactional
    public void moneyTransfer(MoneyTransferDto moneyTransferDto) {
        UUID from = moneyTransferDto.getFromCardId();
        UUID to = moneyTransferDto.getToCardId();
        BigDecimal amount = moneyTransferDto.getAmount();
        cardValidatorService.ensureCardIdNotEquals(from, to);
        Map<UUID, Card> cardById = userService.getCurrentUser().getCards().stream()
                .collect(Collectors.toMap(Card::getId, Function.identity()));
        Card fromCard = cardById.get(from);
        Card toCard = cardById.get(to);
        cardValidatorService.validateTransfer(fromCard, toCard, amount);
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

    }
}
