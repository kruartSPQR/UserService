package com.innowise.user_service.userService.dto.card;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.time.LocalDate;

@Data
public class CardInfoRequestDto {

    @NotNull(message = "Card number is required")
    @CreditCardNumber
    @Size(max = 19, message = "Card number max length is 32")

    private String number;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Card must be valid")
    private LocalDate expirationDate;

    @NotNull(message = "User ID is required")
    private Long userId;

}
