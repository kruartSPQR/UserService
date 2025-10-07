package com.innowise.user_service.userService.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;


@Data
public class UserRequestDto implements Serializable {

//    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name max length is 255")
    private String name;

//    @NotBlank(message = "Surname is required")
    @Size(max = 255, message = "Surname max length is 255")
    private String surname;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email max length is 255")
    private String email;

//    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

}
