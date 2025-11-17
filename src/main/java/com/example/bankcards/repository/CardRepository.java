package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findByUserId(UUID userId);
    boolean existsCardByNumber(String number);

    boolean existsCardById(UUID id);

    Card findFirstByUser_Id(UUID userId);

    boolean existsByUser_Id(UUID id);

    Card findCardByIdAndUser_Id(UUID id, UUID userId);

    Page<Card> findByUserIdAndStatus(UUID userId, CardStatus status, Pageable pageable);
}
