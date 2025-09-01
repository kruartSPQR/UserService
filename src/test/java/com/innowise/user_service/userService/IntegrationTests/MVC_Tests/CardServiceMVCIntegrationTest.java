package com.innowise.user_service.userService.IntegrationTests.MVC_Tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.user_service.userService.controller.CardController;
import com.innowise.user_service.userService.dto.card.CardInfoRequestDto;
import com.innowise.user_service.userService.dto.card.CardInfoResponseDto;
import com.innowise.user_service.userService.service.CardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
public class CardServiceMVCIntegrationTest{

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
   private CardService cardService;

    private CardInfoRequestDto getTestCardRequestDto(){
        CardInfoRequestDto cardInfoRequestDto = new CardInfoRequestDto();
        cardInfoRequestDto.setNumber("4111111111111111");
        cardInfoRequestDto.setUserId(1L);
        cardInfoRequestDto.setExpirationDate(LocalDate.of(2027, 1, 1));
        return cardInfoRequestDto;
    }
    private List<CardInfoResponseDto> getTestCardResponseDtos(){
        CardInfoResponseDto card1 = new CardInfoResponseDto();
        card1.setNumber("4111111111111111");
        card1.setUserId(1L);
        card1.setExpirationDate(LocalDate.of(2027, 1, 1));
        CardInfoResponseDto card2 = new CardInfoResponseDto();
        card2.setNumber("4444444444444444");
        card2.setUserId(2L);
        card2.setExpirationDate(LocalDate.of(2027, 1, 1));
        return List.of(card1, card2);
    }
    @Test
    void shouldCreateCard()  throws Exception {
Mockito.when(cardService.createCard(getTestCardRequestDto()))
      .thenReturn(getTestCardResponseDtos().get(0));

        mockMvc.perform(post("/api/v1/users/cards")
                .content(objectMapper.writeValueAsString(getTestCardRequestDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    @Test
    void shouldUpdateCard()  throws Exception {

        Mockito.when(cardService.updateCardById(1L, getTestCardRequestDto())).thenReturn(getTestCardResponseDtos().get(1));
        mockMvc.perform(put("/api/v1/users/cards/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getTestCardRequestDto())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value("4444444444444444"))
                .andExpect(jsonPath("$.userId").value(2L));
    }
    @Test
    void shouldDeleteCard()  throws Exception {
        Mockito.when(cardService.getCardById(1L)).thenReturn(null);
        mockMvc.perform(delete("/api/v1/users/cards/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
    @Test
    void shouldGetCardById()  throws Exception {
        Mockito.when(cardService.getCardById(1L)).thenReturn(getTestCardResponseDtos().get(0));
        mockMvc.perform(get("/api/v1/users/cards/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.number").value("4111111111111111"))
                .andExpect(jsonPath("$.userId").value(1L));
    }
    @Test
    void shouldGetAllCards()  throws Exception {
        Mockito.when(cardService.getCardsByIds(List.of(1L,2L)))
                .thenReturn(getTestCardResponseDtos());

        mockMvc.perform(get("/api/v1/users/cards").param("ids", "1" ,"2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].number").value("4111111111111111"))
                .andExpect(jsonPath("$.[0].userId").value(1L))
                .andExpect(jsonPath("$.[1].number").value("4444444444444444"))
                .andExpect(jsonPath("$.[1].userId").value(2L));
    }
}

