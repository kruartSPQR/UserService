package com.innowise.user_service.userService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "card_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardInfo {
    @Id
    @Column(name = "card_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

    @Column(nullable = false, unique = true, length = 19)
    String number;

    @Column(nullable = false, length = 255)
    String holder;

    @Column(name = "expiration_date", nullable = false)
    LocalDate expirationDate;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    User user;

}
