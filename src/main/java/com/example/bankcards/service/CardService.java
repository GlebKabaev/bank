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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
        cardValidatorService.validateCardExists(id);
        cardRepository.deleteById(id);
    }

    @Transactional
    public void createCard(CreateCardRequest card) {
        cardValidatorService.ensureCardNotExistsByNumber(card.getNumber());
        Card cardEntity = toCardEntity(card);
        cardValidatorService.validateExpiryDate(cardEntity);
        cardValidatorService.validateCardMatchWithUser(cardEntity);
        cardRepository.save(cardEntity);
    }
    public Card findCardById(UUID id){
        cardValidatorService.validateCardExists(id);
        return cardRepository.findById(id).get();
    }

    @Transactional
    public void blockCard(UUID id) {
        Card card = findCardById(id);
        cardValidatorService.ensureCardStatusNotBlock(card);
        cardValidatorService.validateExpiryDate(card);
        card.setStatus(CardStatus.BLOCKED);
    }

    @Transactional
    public void activateCard(UUID id) {
        Card card = findCardById(id);
        cardValidatorService.ensureCardStatusNotActive(card);
        cardValidatorService.validateExpiryDate(card);
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
        Pageable pageable = PageRequest.of(page - 1, size);
        UUID userId = userService.getCurrentUser().getId();
        Page<Card> cardPage = cardRepository.findByUserIdAndStatus(userId, status, pageable);
        return cardPage.getContent().stream().map(this::toCardDto).toList();
    }


    @Transactional
    public void moneyTransfer(MoneyTransferDto moneyTransferDto) {
        UUID fromId = moneyTransferDto.getFromCardId();
        UUID toId = moneyTransferDto.getToCardId();
        BigDecimal amount = moneyTransferDto.getAmount();
        UUID userId = userService.getCurrentUser().getId();
        Card fromCard = cardRepository.findCardByIdAndUser_Id(fromId, userId);
        Card toCard = cardRepository.findCardByIdAndUser_Id(toId, userId);
        cardValidatorService.validateTransfer(fromCard, toCard, amount);
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

    }
}
