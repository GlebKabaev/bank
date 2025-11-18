package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "Информация о переводе денежных средств между картами")
@NoArgsConstructor
public class MoneyTransferDto {

    @NotNull
    @Schema(description = "ID карты, с которой осуществляется перевод", example = "f3a1c2d4-5b6e-4f78-91a3-cf7d9e2c1234")
    private UUID fromCardId;

    @NotNull
    @Schema(description = "ID карты, на которую осуществляется перевод", example = "a7b2c6d8-9e10-4a5f-bb12-df3f4a6e7890")
    private UUID toCardId;

    @NotNull
    @Positive
    @Schema(description = "Сумма перевода", example = "1500.00")
    private BigDecimal amount;
}
