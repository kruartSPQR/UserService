package com.innowise.user_service.userService.repository;

import com.innowise.user_service.userService.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardRepository extends JpaRepository<CardInfo, Long> {

    @Modifying
    @Query(value = "DELETE FROM CardInfo c WHERE c.id = :id")
    void deleteCardById(@Param("id") Long id);

    @Query(value = "SELECT * FROM card_info WHERE card_id = :id",  nativeQuery = true)
    CardInfo getCardById(@Param("id") Long id);
    Boolean existsByNumber(String number);
}
