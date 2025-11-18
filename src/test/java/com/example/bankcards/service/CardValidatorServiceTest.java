package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardValidatorServiceTest {

    @Mock
    private CardRepository cardRepository;

    private CardValidatorService cardValidatorService;

    private String cardExistsByNumberMessage = "Card already exists by number";
    private String cardNotFoundByNumberMessage = "Card not found by number";
    private String cardNotFoundByIdMessage = "Card not found by id";
    private String wrongCardOwnerException = "Wrong card owner";
    private String cardEqualIdExceptionMessage = "Card ids are equal";
    private String notEnoughBalanceExceptionMessage = "Not enough balance";
    private String wrongCardExpiryDate = "Card expired";
    private String cardStatusException = "Invalid card status";

    @BeforeEach
    void setUp() {
        cardValidatorService = new CardValidatorService(
                cardExistsByNumberMessage,
                cardRepository,
                cardNotFoundByNumberMessage,
                cardNotFoundByIdMessage,
                wrongCardOwnerException,
                cardEqualIdExceptionMessage,
                notEnoughBalanceExceptionMessage,
                wrongCardExpiryDate,
                cardStatusException
        );
    }

    @Test
    void ensureCardNotExistsByNumber_whenCardDoesNotExist_shouldNotThrow() {
        String number = "1234567890123456";
        when(cardRepository.existsCardByNumber(number)).thenReturn(false);

        assertDoesNotThrow(() -> cardValidatorService.ensureCardNotExistsByNumber(number));
    }

    @Test
    void ensureCardNotExistsByNumber_whenCardExists_shouldThrowCardAlreadyExistsException() {
        String number = "1234567890123456";
        when(cardRepository.existsCardByNumber(number)).thenReturn(true);

        assertThrows(CardAlreadyExistsException.class, () -> cardValidatorService.ensureCardNotExistsByNumber(number));
    }

    @Test
    void validateCardExistsByNumber_whenCardExists_shouldNotThrow() {
        String number = "1234567890123456";
        when(cardRepository.existsCardByNumber(number)).thenReturn(true);

        assertDoesNotThrow(() -> cardValidatorService.validateCardExists(number));
    }

    @Test
    void validateCardExistsByNumber_whenCardDoesNotExist_shouldThrowCardNotFoundException() {
        String number = "1234567890123456";
        when(cardRepository.existsCardByNumber(number)).thenReturn(false);

        assertThrows(CardNotFoundException.class, () -> cardValidatorService.validateCardExists(number));
    }

    @Test
    void validateCardExistsById_whenCardExists_shouldNotThrow() {
        UUID id = UUID.randomUUID();
        when(cardRepository.existsCardById(id)).thenReturn(true);

        assertDoesNotThrow(() -> cardValidatorService.validateCardExists(id));
    }

    @Test
    void validateCardExistsById_whenCardDoesNotExist_shouldThrowCardNotFoundException() {
        UUID id = UUID.randomUUID();
        when(cardRepository.existsCardById(id)).thenReturn(false);

        assertThrows(CardNotFoundException.class, () -> cardValidatorService.validateCardExists(id));
    }

    @Test
    void validateCardMatchWithUser_whenNoCards_shouldNotThrow() {
        Card card = createCard();
        when(cardRepository.existsByUser_Id(card.getUser().getId())).thenReturn(false);

        assertDoesNotThrow(() -> cardValidatorService.validateCardMatchWithUser(card));
    }

    @Test
    void validateCardMatchWithUser_whenHasCardsAndOwnerMatches_shouldNotThrow() {
        Card card = createCard();
        Card existingCard = createCard();
        existingCard.setOwner(card.getOwner());
        when(cardRepository.existsByUser_Id(card.getUser().getId())).thenReturn(true);
        when(cardRepository.findFirstByUser_Id(card.getUser().getId())).thenReturn(existingCard);

        assertDoesNotThrow(() -> cardValidatorService.validateCardMatchWithUser(card));
    }

    @Test
    void validateCardMatchWithUser_whenHasCardsAndOwnerDoesNotMatch_shouldThrowCardOwnerException() {
        Card card = createCard();
        Card existingCard = createCard();
        existingCard.setOwner("Different Owner");
        when(cardRepository.existsByUser_Id(card.getUser().getId())).thenReturn(true);
        when(cardRepository.findFirstByUser_Id(card.getUser().getId())).thenReturn(existingCard);

        assertThrows(CardOwnerException.class, () -> cardValidatorService.validateCardMatchWithUser(card));
    }

    @Test
    void ensureCardIdNotEquals_whenIdsDifferent_shouldNotThrow() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        assertDoesNotThrow(() -> cardValidatorService.ensureCardIdNotEquals(id1, id2));
    }

    @Test
    void ensureCardIdNotEquals_whenIdsEqual_shouldThrowCardException() {
        UUID id = UUID.randomUUID();

        assertThrows(CardException.class, () -> cardValidatorService.ensureCardIdNotEquals(id, id));
    }

    @Test
    void validateTransfer_whenValid_shouldNotThrow() {
        Card from = createCard();
        from.setBalance(BigDecimal.valueOf(100));
        from.setStatus(CardStatus.ACTIVE);
        from.setExpiryYear(2026);
        from.setExpiryMonth(1);
        Card to = createCard();
        to.setStatus(CardStatus.ACTIVE);
        to.setExpiryYear(2026);
        to.setExpiryMonth(1);
        BigDecimal amount = BigDecimal.valueOf(50);

        assertDoesNotThrow(() -> cardValidatorService.validateTransfer(from, to, amount));
    }

    @Test
    void validateTransfer_whenFromNull_shouldThrowCardNotFoundException() {
        Card to = createCard();
        BigDecimal amount = BigDecimal.valueOf(50);

        assertThrows(CardNotFoundException.class, () -> cardValidatorService.validateTransfer(null, to, amount));
    }

    @Test
    void validateTransfer_whenToNull_shouldThrowCardNotFoundException() {
        Card from = createCard();
        BigDecimal amount = BigDecimal.valueOf(50);

        assertThrows(CardNotFoundException.class, () -> cardValidatorService.validateTransfer(from, null, amount));
    }

    @Test
    void validateTransfer_whenNotEnoughBalance_shouldThrowNotEnoughBalanceException() {
        Card from = createCard();
        from.setBalance(BigDecimal.valueOf(40));
        from.setStatus(CardStatus.ACTIVE);
        from.setExpiryYear(2026);
        Card to = createCard();
        to.setStatus(CardStatus.ACTIVE);
        to.setExpiryYear(2026);
        BigDecimal amount = BigDecimal.valueOf(50);

        assertThrows(NotEnoughBalanceException.class, () -> cardValidatorService.validateTransfer(from, to, amount));
    }

    @Test
    void validateTransfer_whenFromNotActive_shouldThrowCardStatusException() {
        Card from = createCard();
        from.setBalance(BigDecimal.valueOf(100));
        from.setStatus(CardStatus.BLOCKED);
        from.setExpiryYear(2026);
        Card to = createCard();
        to.setStatus(CardStatus.ACTIVE);
        to.setExpiryYear(2026);
        BigDecimal amount = BigDecimal.valueOf(50);

        assertThrows(CardStatusException.class, () -> cardValidatorService.validateTransfer(from, to, amount));
    }

    @Test
    void validateTransfer_whenToNotActive_shouldThrowCardStatusException() {
        Card from = createCard();
        from.setBalance(BigDecimal.valueOf(100));
        from.setStatus(CardStatus.ACTIVE);
        from.setExpiryYear(2026);
        Card to = createCard();
        to.setStatus(CardStatus.BLOCKED);
        to.setExpiryYear(2026);
        BigDecimal amount = BigDecimal.valueOf(50);

        assertThrows(CardStatusException.class, () -> cardValidatorService.validateTransfer(from, to, amount));
    }

    @Test
    void validateTransfer_whenFromExpired_shouldThrowCardExpiryDateException() {
        Card from = createCard();
        from.setBalance(BigDecimal.valueOf(100));
        from.setStatus(CardStatus.ACTIVE);
        from.setExpiryYear(2025);
        from.setExpiryMonth(10);
        Card to = createCard();
        to.setStatus(CardStatus.ACTIVE);
        to.setExpiryYear(2026);
        BigDecimal amount = BigDecimal.valueOf(50);

        assertThrows(CardExpiryDateException.class, () -> cardValidatorService.validateTransfer(from, to, amount));
    }

    @Test
    void validateTransfer_whenToExpired_shouldThrowCardExpiryDateException() {
        Card from = createCard();
        from.setBalance(BigDecimal.valueOf(100));
        from.setStatus(CardStatus.ACTIVE);
        from.setExpiryYear(2026);
        Card to = createCard();
        to.setStatus(CardStatus.ACTIVE);
        to.setExpiryYear(2025);
        to.setExpiryMonth(10);
        BigDecimal amount = BigDecimal.valueOf(50);

        assertThrows(CardExpiryDateException.class, () -> cardValidatorService.validateTransfer(from, to, amount));
    }

    @Test
    void validateExpiryDate_whenNotExpired_shouldNotThrow() {
        Card card = createCard();
        card.setExpiryYear(2025);
        card.setExpiryMonth(12);

        assertDoesNotThrow(() -> cardValidatorService.validateExpiryDate(card));
    }

    @Test
    void validateExpiryDate_whenExpired_shouldThrowCardExpiryDateException() {
        Card card = createCard();
        card.setExpiryYear(2025);
        card.setExpiryMonth(10);

        assertThrows(CardExpiryDateException.class, () -> cardValidatorService.validateExpiryDate(card));
    }

    @Test
    void validateExpiryDate_whenYearExpired_shouldThrowCardExpiryDateException() {
        Card card = createCard();
        card.setExpiryYear(2024);
        card.setExpiryMonth(12);

        assertThrows(CardExpiryDateException.class, () -> cardValidatorService.validateExpiryDate(card));
    }

    @Test
    void validateCardStatus_whenStatusMatches_shouldNotThrow() {
        Card card = createCard();
        card.setStatus(CardStatus.ACTIVE);

        assertDoesNotThrow(() -> cardValidatorService.validateCardStatus(card, CardStatus.ACTIVE));
    }

    @Test
    void validateCardStatus_whenStatusDoesNotMatch_shouldThrowCardStatusException() {
        Card card = createCard();
        card.setStatus(CardStatus.BLOCKED);

        assertThrows(CardStatusException.class, () -> cardValidatorService.validateCardStatus(card, CardStatus.ACTIVE));
    }

    @Test
    void ensureCardStatusNotBlock_whenNotBlocked_shouldNotThrow() {
        Card card = createCard();
        card.setStatus(CardStatus.ACTIVE);

        assertDoesNotThrow(() -> cardValidatorService.ensureCardStatusNotBlock(card));
    }

    @Test
    void ensureCardStatusNotBlock_whenBlocked_shouldThrowCardStatusException() {
        Card card = createCard();
        card.setStatus(CardStatus.BLOCKED);

        assertThrows(CardStatusException.class, () -> cardValidatorService.ensureCardStatusNotBlock(card));
    }

    @Test
    void ensureCardStatusNotActive_whenNotBlocked_shouldNotThrow() {
        Card card = createCard();
        card.setStatus(CardStatus.ACTIVE);

        assertDoesNotThrow(() -> cardValidatorService.ensureCardStatusNotActive(card));
    }

    @Test
    void ensureCardStatusNotActive_whenBlocked_shouldThrowCardStatusException() {
        Card card = createCard();
        card.setStatus(CardStatus.BLOCKED);

        assertThrows(CardStatusException.class, () -> cardValidatorService.ensureCardStatusNotActive(card));
    }

    private Card createCard() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        return Card.builder()
                .id(UUID.randomUUID())
                .number("1234567890123456")
                .owner("Ivan Ivanov")
                .expiryMonth(12)
                .expiryYear(2025)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();
    }
}