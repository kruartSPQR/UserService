package com.innowise.user_service.userService.dto.card;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class CardInfoResponseDto implements Serializable {

    private Long id;

    private String number;

    private String holder;

    private LocalDate expirationDate;

    private Long userId;

}
