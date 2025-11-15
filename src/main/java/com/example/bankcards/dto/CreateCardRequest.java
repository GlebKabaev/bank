package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
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
public class CreateCardRequest {
    @NotBlank
    @Size(min = 16,max = 16)
    private String number;

    @NotBlank
    private String owner;

    @Min(value = 1)
    @Max(value = 12)
    private int expiryMonth;

    @Min(value = 2025)
    private int expiryYear;

    @NotNull
    private CardStatus status;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal balance;

    @NotNull
    private UUID userId;
}