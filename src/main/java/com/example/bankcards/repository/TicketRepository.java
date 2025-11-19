package com.example.bankcards.repository;

import com.example.bankcards.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    boolean existsByCard_Id(UUID cardId);

    void deleteByCard_Id(UUID cardId);
}
