package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
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
@Schema(description = "Запрос на создание новой банковской карты")
public class CreateCardRequest {

    @NotBlank
    @Size(min = 16, max = 16)
    @Schema(description = "Полный номер карты (16 цифр)", example = "4111111111111111")
    private String number;

    @NotBlank
    @Schema(description = "Имя владельца карты", example = "IVAN IVANOV")
    private String owner;

    @Min(1)
    @Max(12)
    @Schema(description = "Месяц окончания срока действия карты", example = "7")
    private int expiryMonth;

    @Schema(description = "Год окончания срока действия карты", example = "2027")
    private int expiryYear;

    @NotNull
    @Schema(description = "Статус создаваемой карты", example = "ACTIVE")
    private CardStatus status;

    @NotNull
    @DecimalMin("0.00")
    @Schema(description = "Начальный баланс карты", example = "0.00")
    private BigDecimal balance;

    @NotNull
    @Schema(description = "Идентификатор пользователя, которому принадлежит карта", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID userId;
}
