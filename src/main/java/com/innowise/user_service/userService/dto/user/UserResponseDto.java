package com.innowise.user_service.userService.dto.user;

import com.innowise.user_service.userService.dto.card.CardInfoResponseDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserResponseDto {

    private Long id;

    private String name;

    private String surname;

    private String email;

    private LocalDate birthDate;

    private List<CardInfoResponseDto> cardList = new ArrayList<>();

}
