package com.example.bankcards.dto;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDto {

    private UUID id;
    private String maskedNumber;
    private String owner;
    private int expiryMonth;
    private int expiryYear;
    private CardStatus status;
    private BigDecimal balance;

}