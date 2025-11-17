package com.example.bankcards.service;

import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class CardValidatorService {
    private final CardRepository cardRepository;
    private final String cardExistsByNumberMessage;
    private final String cardNotFoundByNumberMessage;
    private final String cardNotFoundByIdMessage;
    private final String wrongCardOwnerException;
    private final String cardEqualIdExceptionMessage;
    private final String notEnoughBalanceExceptionMessage;
    private final String wrongCardExpiryDate;
    private final String cardStatusException;

    public CardValidatorService(@Value("${app.card.exception-message.already-exists-by.number}") String cardExistsByNumberMessage,
                                CardRepository cardRepository,
                                @Value("${app.card.exception-message.not-found-by.number}") String cardNotFoundByNumberMessage,
                                @Value("${app.card.exception-message.not-found-by.Id}") String cardNotFoundByIdMessage,
                                @Value("${app.card.exception-message.wrong-owner-exception}") String wrongCardOwnerException,
                                @Value("${app.card.exception-message.equal-id}") String cardEqualIdExceptionMessage,
                                @Value("${app.card.exception-message.not-enough-balance}") String notEnoughBalanceExceptionMessage,
                                @Value("${app.card.exception-message.card-expiry}") String wrongCardExpiryDate,
                                @Value("${app.card.exception-message.card-status}") String cardStatusException) {
        this.cardExistsByNumberMessage = cardExistsByNumberMessage;
        this.cardRepository = cardRepository;
        this.cardNotFoundByNumberMessage = cardNotFoundByNumberMessage;
        this.cardNotFoundByIdMessage = cardNotFoundByIdMessage;
        this.wrongCardOwnerException = wrongCardOwnerException;
        this.cardEqualIdExceptionMessage = cardEqualIdExceptionMessage;
        this.notEnoughBalanceExceptionMessage = notEnoughBalanceExceptionMessage;
        this.wrongCardExpiryDate = wrongCardExpiryDate;
        this.cardStatusException = cardStatusException;
    }

    public void ensureCardNotExistsByNumber(String number) {
        if (cardRepository.existsCardByNumber(number)) {
            throw new CardAlreadyExistsException(cardExistsByNumberMessage);
        }
    }

    public void validateCardExists(String number) {
        if (!cardRepository.existsCardByNumber(number)) {
            throw new CardNotFoundException(cardNotFoundByNumberMessage);
        }
    }

    public void validateCardExists(UUID id) {
        if (!cardRepository.existsCardById(id)) {
            throw new CardNotFoundException(cardNotFoundByIdMessage);
        }
    }

    public void validateCardMatchWithUser(Card card) {
        boolean hasAnyCard = cardRepository.existsByUser_Id(card.getUser().getId());

        if (hasAnyCard && !cardRepository.findFirstByUser_Id(card.getUser().getId())
                .getOwner()
                .equals(card.getOwner())) {
            throw new CardOwnerException(wrongCardOwnerException);
        }
    }

    public void ensureCardIdNotEquals(UUID card1, UUID card2) {
        if (card1.equals(card2)) {
            throw new CardException(cardEqualIdExceptionMessage);
        }
    }

    public void validateTransfer(Card from, Card to, BigDecimal amount) {
        if (from == null) {
            throw new CardNotFoundException(cardNotFoundByIdMessage);
        }
        if (to == null) {
            throw new CardNotFoundException(cardNotFoundByIdMessage);
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new NotEnoughBalanceException(notEnoughBalanceExceptionMessage);
        }

    }

    public void validateExpiryDate(CreateCardRequest card) {
        LocalDate today = LocalDate.now();
        int todayYear = today.getYear();
        int todayMonth = today.getMonthValue();
        if ((card.getExpiryYear() == todayYear && card.getExpiryMonth() < todayMonth) || (card.getExpiryYear() < todayYear)) {
            throw new CardExpiryDateException(wrongCardExpiryDate);
        }
    }

    public void validateCardStatus(Card card, CardStatus preferredStatus) {
        if (!card.getStatus().equals(preferredStatus)) {
            throw new CardStatusException(cardStatusException);
        }

    }

}
