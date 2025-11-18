package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Информация о запросе на создание тикета для карты")
public class TicketDto {

    @NotNull
    @Schema(description = "ID карты, для которой создается тикет блокировки", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID cardId;
}
