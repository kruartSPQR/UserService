package com.innowise.user_service.userService.controller;

import com.innowise.user_service.userService.dto.card.CardInfoRequestDto;
import com.innowise.user_service.userService.dto.card.CardInfoResponseDto;
import com.innowise.user_service.userService.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/users/cards")
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardInfoResponseDto> createCard(@Valid @RequestBody CardInfoRequestDto cardInfo) {
        return new ResponseEntity<>(cardService.createCard(cardInfo), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CardInfoResponseDto>> getCardsByIds(@Valid @Size(max = 100) @RequestParam List<Long> ids) {
        return new ResponseEntity<>(cardService.getCardsByIds(ids), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> getCardById(@PathVariable Long id) {
        return new ResponseEntity<>(cardService.getCardById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> updateCardInfoById(@PathVariable Long id, @Valid @RequestBody CardInfoRequestDto cardInfo) {
        return new ResponseEntity<>(cardService.updateCardById(id, cardInfo), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCardById(@PathVariable Long id) {
        cardService.deleteCardById(id);
    }
}