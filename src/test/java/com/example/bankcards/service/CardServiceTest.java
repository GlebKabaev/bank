package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.dto.MoneyTransferDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardValidatorService cardValidatorService;

    @Mock
    private UserValidatorService userValidatorService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardService cardService;

    private UUID cardId;
    private UUID userId;
    private Card card;
    private User user;
    private CreateCardRequest createCardRequest;
    private MoneyTransferDto moneyTransferDto;

    @BeforeEach
    void setUp() {
        cardId = UUID.randomUUID();
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        card = Card.builder()
                .id(cardId)
                .number("1234567890123456")
                .owner("Ivan Ivanov")
                .expiryMonth(12)
                .expiryYear(2025)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .user(user)
                .build();

        createCardRequest = new CreateCardRequest();
        createCardRequest.setNumber("1234567890123456");
        createCardRequest.setOwner("Ivan Ivanov");
        createCardRequest.setExpiryMonth(12);
        createCardRequest.setExpiryYear(2025);
        createCardRequest.setStatus(CardStatus.ACTIVE);
        createCardRequest.setBalance(BigDecimal.valueOf(1000));
        createCardRequest.setUserId(userId);

        moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setFromCardId(cardId);
        moneyTransferDto.setToCardId(UUID.randomUUID());
        moneyTransferDto.setAmount(BigDecimal.valueOf(100));
    }

    @Test
    void findAllCards_shouldReturnListOfCardDtos() {
        List<Card> cards = List.of(card);
        when(cardRepository.findAll()).thenReturn(cards);

        List<CardDto> result = cardService.findAllCards();

        assertEquals(1, result.size());
        assertEquals(cardId, result.get(0).getId());
        assertEquals("**** **** **** 3456", result.get(0).getMaskedNumber());
    }

    @Test
    void findCardsByUser_shouldReturnListOfCardDtos() {
        doNothing().when(userValidatorService).validateUserExistsById(userId);
        List<Card> cards = List.of(card);
        when(cardRepository.findByUserId(userId)).thenReturn(cards);

        List<CardDto> result = cardService.findCardsByUser(userId);

        assertEquals(1, result.size());
        assertEquals(cardId, result.get(0).getId());
    }

    @Test
    void deleteCard_shouldDeleteCard() {
        doNothing().when(cardValidatorService).validateCardExists(cardId);
        doNothing().when(cardRepository).deleteById(cardId);

        cardService.deleteCard(cardId);

        verify(cardRepository).deleteById(cardId);
    }

    @Test
    void createCard_shouldSaveCard() {
        doNothing().when(cardValidatorService).ensureCardNotExistsByNumber(createCardRequest.getNumber());
        doNothing().when(userValidatorService).validateUserExistsById(userId);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        doNothing().when(cardValidatorService).validateExpiryDate(any(Card.class));
        doNothing().when(cardValidatorService).validateCardMatchWithUser(any(Card.class));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.createCard(createCardRequest);

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void findCardById_shouldReturnCard() {
        doNothing().when(cardValidatorService).validateCardExists(cardId);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        Card result = cardService.findCardById(cardId);

        assertEquals(cardId, result.getId());
    }

    @Test
    void blockCard_shouldBlockCard() {
        doNothing().when(cardValidatorService).validateCardExists(cardId);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        doNothing().when(cardValidatorService).ensureCardStatusNotBlock(card);
        doNothing().when(cardValidatorService).validateExpiryDate(card);

        cardService.blockCard(cardId);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).findById(cardId);
    }

    @Test
    void activateCard_shouldActivateCard() {
        card.setStatus(CardStatus.BLOCKED);
        doNothing().when(cardValidatorService).validateCardExists(cardId);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        doNothing().when(cardValidatorService).ensureCardStatusNotActive(card);
        doNothing().when(cardValidatorService).validateExpiryDate(card);

        cardService.activateCard(cardId);

        assertEquals(CardStatus.ACTIVE, card.getStatus());
    }

    @Test
    void toCardDto_shouldConvertToDto() {
        CardDto dto = cardService.toCardDto(card);

        assertEquals(cardId, dto.getId());
        assertEquals("**** **** **** 3456", dto.getMaskedNumber());
        assertEquals("Ivan Ivanov", dto.getOwner());
        assertEquals(12, dto.getExpiryMonth());
        assertEquals(2025, dto.getExpiryYear());
        assertEquals(CardStatus.ACTIVE, dto.getStatus());
        assertEquals(BigDecimal.valueOf(1000), dto.getBalance());
    }

    @Test
    void toCardEntity_shouldConvertToEntity() {
        doNothing().when(userValidatorService).validateUserExistsById(userId);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        Card entity = cardService.toCardEntity(createCardRequest);

        assertNotNull(entity.getId());
        assertEquals("1234567890123456", entity.getNumber());
        assertEquals("Ivan Ivanov", entity.getOwner());
        assertEquals(12, entity.getExpiryMonth());
        assertEquals(2025, entity.getExpiryYear());
        assertEquals(CardStatus.ACTIVE, entity.getStatus());
        assertEquals(BigDecimal.valueOf(1000), entity.getBalance());
        assertEquals(user, entity.getUser());
    }

    @Test
    void getUsersCard_shouldReturnPagedCards() {
        int page = 1;
        CardStatus status = CardStatus.ACTIVE;
        Pageable pageable = PageRequest.of(page - 1, 2);
        List<Card> cards = List.of(card);
        Page<Card> cardPage = new PageImpl<>(cards);
        when(userService.getCurrentUser()).thenReturn(user);
        when(cardRepository.findByUserIdAndStatus(userId, status, pageable)).thenReturn(cardPage);

        List<CardDto> result = cardService.getUsersCard(page, status);

        assertEquals(1, result.size());
        assertEquals(cardId, result.get(0).getId());
    }

    @Test
    void moneyTransfer_shouldTransferMoney() {
        UUID toCardId = moneyTransferDto.getToCardId();
        Card toCard = new Card();
        toCard.setId(toCardId);
        toCard.setBalance(BigDecimal.valueOf(500));

        when(userService.getCurrentUser()).thenReturn(user);
        when(cardRepository.findCardByIdAndUser_Id(cardId, userId)).thenReturn(card);
        when(cardRepository.findCardByIdAndUser_Id(toCardId, userId)).thenReturn(toCard);
        doNothing().when(cardValidatorService).validateTransfer(card, toCard, moneyTransferDto.getAmount());

        cardService.moneyTransfer(moneyTransferDto);

        assertEquals(BigDecimal.valueOf(900), card.getBalance());
        assertEquals(BigDecimal.valueOf(600), toCard.getBalance());
    }
}