package com.innowise.user_service.userService.IntegrationTests;

import com.innowise.user_service.userService.dto.card.CardInfoRequestDto;
import com.innowise.user_service.userService.dto.card.CardInfoResponseDto;
import com.innowise.user_service.userService.dto.user.UserRequestDto;
import com.innowise.user_service.userService.dto.user.UserResponseDto;
import com.innowise.user_service.userService.exception.DuplicateResourceCustomException;
import com.innowise.user_service.userService.exception.ResourceNotFoundCustomException;
import com.innowise.user_service.userService.service.CardService;
import com.innowise.user_service.userService.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    private UserResponseDto createTestUser() {
        UserRequestDto userRequest = new UserRequestDto();
        userRequest.setName("CardUser");
        userRequest.setSurname("Test");
        userRequest.setEmail("card.user@example.com");
        userRequest.setBirthDate(LocalDate.of(1985, 5, 15));
        return userService.createUser(userRequest);
    }

    private CardInfoRequestDto createTestCardDto(Long userId) {
        CardInfoRequestDto dto = new CardInfoRequestDto();
        dto.setNumber("4111111111111111");
        dto.setExpirationDate(LocalDate.now().plusYears(2));
        dto.setUserId(userId);
        return dto;
    }

    @Test
    void shouldCreateCard() {
        UserResponseDto user = createTestUser();

        CardInfoRequestDto cardRequest = createTestCardDto(user.getId());
        CardInfoResponseDto cardResponse = cardService.createCard(cardRequest);

        assertNotNull(cardResponse.getId());
        assertEquals(user.getId(), cardResponse.getUserId());
        assertEquals("CARDUSER TEST", cardResponse.getHolder());

        CardInfoResponseDto byId = cardService.getCardById(cardResponse.getId());
        assertEquals(cardResponse.getNumber(), byId.getNumber());
    }

    @Test
    void shouldUpdateCard() {
        UserResponseDto user = createTestUser();
        CardInfoResponseDto card = cardService.createCard(createTestCardDto(user.getId()));

        CardInfoRequestDto updateRequest = createTestCardDto(user.getId());
        updateRequest.setExpirationDate(LocalDate.now().plusYears(5));

        CardInfoResponseDto updated = cardService.updateCardById(card.getId(), updateRequest);

        assertEquals(updateRequest.getExpirationDate(), updated.getExpirationDate());
    }

    @Test
    void shouldDeleteCard() {
        UserResponseDto user = createTestUser();
        CardInfoResponseDto card = cardService.createCard(createTestCardDto(user.getId()));

        cardService.deleteCardById(card.getId());

        assertThrows(ResourceNotFoundCustomException.class,
                () -> cardService.getCardById(card.getId()));
    }

    @Test
    void shouldGetCards() {
        UserResponseDto user = createTestUser();

        CardInfoRequestDto card1 = createTestCardDto(user.getId());
        card1.setNumber("4111111111111111");

        CardInfoRequestDto card2 = createTestCardDto(user.getId());
        card2.setNumber("5500000000000004");

        CardInfoResponseDto created1 = cardService.createCard(card1);
        CardInfoResponseDto created2 = cardService.createCard(card2);

        List<CardInfoResponseDto> cards = cardService.getCardsByIds(
                List.of(created1.getId(), created2.getId())
        );

        assertEquals(2, cards.size());
        assertTrue(cards.stream().anyMatch(c -> c.getNumber().equals("4111111111111111")));
        assertTrue(cards.stream().anyMatch(c -> c.getNumber().equals("5500000000000004")));
    }

    @Test
    void shouldPreventDuplicateCardNumber() {
        UserResponseDto user = createTestUser();
        cardService.createCard(createTestCardDto(user.getId()));

        assertThrows(DuplicateResourceCustomException.class,
                () -> cardService.createCard(createTestCardDto(user.getId())));
    }

    @Test
    void shouldGiveExceptionWhenUserNotFound() {
        CardInfoRequestDto cardRequest = createTestCardDto(999L);

        assertThrows(ResourceNotFoundCustomException.class,
                () -> cardService.createCard(cardRequest));
    }

    @Test
    void shouldRemoveCardFromUserWhenDeleted() {
        UserResponseDto user = createTestUser();
        CardInfoRequestDto tempCard = createTestCardDto(user.getId());
        CardInfoResponseDto card = cardService.createCard(tempCard);

        UserResponseDto userWithCards = userService.getUserById(user.getId());
        assertEquals(1, userWithCards.getCardList().size());

        cardService.deleteCardById(card.getId());

        UserResponseDto userAfterDelete = userService.getUserById(user.getId());
        assertTrue(userAfterDelete.getCardList().isEmpty());
    }

    @Test
    void shouldGenerateCorrectCardHolder() {
        UserResponseDto user = createTestUser();
        CardInfoResponseDto card = cardService.createCard(createTestCardDto(user.getId()));

        assertEquals("CARDUSER TEST", card.getHolder());
    }
}