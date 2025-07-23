package com.innowise.user_service.userService.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NamedQuery(
        name = "User.deleteUserById",
        query = "DELETE FROM User u WHERE u.id = :id"
)

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String surname;

    @Column(nullable = false, length = 255)
    private String email;
    private LocalDate birthDate;

    @OneToMany( mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardInfo> cardList = new ArrayList<>();

}