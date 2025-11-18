package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Информация о банковской карте")
public class CardDto {

    @Schema(description = "Уникальный идентификатор карты", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Маскированный номер карты", example = "**** **** **** 1111")
    private String maskedNumber;

    @Schema(description = "Имя владельца карты", example = "IVAN IVANOV")
    private String owner;

    @Schema(description = "Месяц окончания срока действия карты", example = "12")
    private int expiryMonth;

    @Schema(description = "Год окончания срока действия карты", example = "2026")
    private int expiryYear;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Текущий баланс карты", example = "15000.75")
    private BigDecimal balance;
}
