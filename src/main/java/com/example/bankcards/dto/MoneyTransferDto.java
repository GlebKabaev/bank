package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MoneyTransferDto {
    @NotNull
    private UUID fromCardId;

    @NotNull
    private UUID toCardId;

    @NotNull
    @Positive
    private BigDecimal amount;
}
