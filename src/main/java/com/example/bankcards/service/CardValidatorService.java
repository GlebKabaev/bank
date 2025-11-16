package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardAlreadyExistsException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.WrongCardOwnerException;
import com.example.bankcards.repository.CardRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CardValidatorService {
    private final CardRepository cardRepository;
    private final String cardExistsByNumberMessage;
    private final String cardNotFoundByNumberMessage;
    private final String cardNotFoundByIdMessage;
    private final String wrongCardOwnerException;
    public CardValidatorService(@Value("${app.card.exception-message.already-exists-by.number}") String cardExistsByNumberMessage,
                                CardRepository cardRepository,
                                @Value("${app.card.exception-message.not-found-by.number}") String cardNotFoundByNumberMessage,
                                @Value("${app.card.exception-message.not-found-by.Id}") String cardNotFoundByIdMessage,
                                @Value("${app.card.exception-message.wrong-owner-exception}") String wrongCardOwnerException) {
        this.cardExistsByNumberMessage = cardExistsByNumberMessage;
        this.cardRepository = cardRepository;
        this.cardNotFoundByNumberMessage = cardNotFoundByNumberMessage;
        this.cardNotFoundByIdMessage = cardNotFoundByIdMessage;
        this.wrongCardOwnerException = wrongCardOwnerException;
    }

    public void ensureCardNotExistsByNumber(String number) {
        if (cardRepository.existsCardByNumber(number)) {
            throw new CardAlreadyExistsException(cardExistsByNumberMessage);
        }
    }

    public void validateCardExistsByNumber(String number) {
        if (!cardRepository.existsCardByNumber(number)) {
            throw new CardNotFoundException(cardNotFoundByNumberMessage);
        }
    }

    public void validateCardExistsById(UUID id) {
        if (!cardRepository.existsCardById(id)) {
            throw new CardNotFoundException(cardNotFoundByIdMessage);
        }
    }
    public void validateCardMatchWithUser(Card card){
        if (!card.getUser().getCards().getFirst().getOwner().equals(card.getOwner())&& !card.getUser().getCards().isEmpty()){
            throw new WrongCardOwnerException(wrongCardOwnerException);
        }
    }

}
