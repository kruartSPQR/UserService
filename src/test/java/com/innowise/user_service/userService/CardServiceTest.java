package com.innowise.user_service.userService;

import com.innowise.user_service.userService.dto.card.CardInfoRequestDto;
import com.innowise.user_service.userService.dto.card.CardInfoResponseDto;
import com.innowise.user_service.userService.entity.CardInfo;
import com.innowise.user_service.userService.entity.User;
import com.innowise.user_service.userService.exception.DuplicateResourceCustomException;
import com.innowise.user_service.userService.mapper.CardInfoMapper;
import com.innowise.user_service.userService.repository.CardRepository;
import com.innowise.user_service.userService.repository.UserRepository;
import com.innowise.user_service.userService.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardInfoMapper cardInfoMapper;



    @InjectMocks
    private CardService cardService;

    private CardInfoRequestDto cardRequestDto;
    private CardInfo cardEntity;
    private CardInfoResponseDto cardResponseDto;
    private User userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new User();
        userEntity.setId(1L);
        userEntity.setName("John");
        userEntity.setSurname("Doe");

        cardRequestDto = new CardInfoRequestDto();
        cardRequestDto.setUserId(1L);
        cardRequestDto.setNumber("1234567890123456");
        cardRequestDto.setExpirationDate(LocalDate.of(26, 12, 31));

        cardEntity = new CardInfo();
        cardEntity.setId(1L);
        cardEntity.setNumber("1234567890123456");
        cardEntity.setExpirationDate(LocalDate.of(25, 12, 31));
        cardEntity.setUser(userEntity);
        cardEntity.setHolder("JOHN DOE");

        cardResponseDto = new CardInfoResponseDto();
        cardResponseDto.setId(1L);
        cardResponseDto.setNumber("1234567890123456");
        cardResponseDto.setExpirationDate(LocalDate.of(25, 12, 31));
        cardResponseDto.setHolder("JOHN DOE");


    }

    @Test
    void testCreateCard() {
        when(userRepository.getUserById(cardRequestDto.getUserId())).thenReturn(userEntity);
        when(cardRepository.existsByNumber(cardRequestDto.getNumber())).thenReturn(false);
        when(cardInfoMapper.toEntity(cardRequestDto)).thenReturn(cardEntity);
        when(cardRepository.save(cardEntity)).thenReturn(cardEntity);
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardResponseDto);

        CardInfoResponseDto result = cardService.createCard(cardRequestDto);

        assertNotNull(result);
        assertEquals(cardResponseDto.getNumber(), result.getNumber());
        assertEquals(cardResponseDto.getExpirationDate(), result.getExpirationDate());
        assertEquals(cardResponseDto.getHolder(), result.getHolder());

        verify(userRepository).getUserById(cardRequestDto.getUserId());
        verify(cardRepository).existsByNumber(cardRequestDto.getNumber());
        verify(cardInfoMapper).toEntity(cardRequestDto);
        verify(cardRepository).save(cardEntity);
        verify(cardInfoMapper).toDto(cardEntity);
    }

    @Test
    void testCreateCardWithDuplicateException() {
        when(userRepository.getUserById(cardRequestDto.getUserId())).thenReturn(userEntity);
        when(cardRepository.existsByNumber(cardRequestDto.getNumber())).thenReturn(true);

        DuplicateResourceCustomException exception = assertThrows(DuplicateResourceCustomException.class, () -> {
            cardService.createCard(cardRequestDto);
        });

        assertEquals("Card number already exists", exception.getMessage());

        verify(userRepository).getUserById(cardRequestDto.getUserId());
        verify(cardRepository).existsByNumber(cardRequestDto.getNumber());
        verify(cardRepository, never()).save(any());
        verify(cardInfoMapper, never()).toDto(any());
    }

    @Test
    void testGetCardById() {
        when(cardRepository.getCardById(1L)).thenReturn(cardEntity);
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardResponseDto);

        CardInfoResponseDto result = cardService.getCardById(1L);

        assertNotNull(result);
        assertEquals(cardResponseDto.getNumber(), result.getNumber());
        assertEquals(cardResponseDto.getExpirationDate(), result.getExpirationDate());
        assertEquals(cardResponseDto.getHolder(), result.getHolder());

        verify(cardRepository).getCardById(1L);
        verify(cardInfoMapper).toDto(cardEntity);
    }

    @Test
    void testGetCardsByIds() {
        List<Long> ids = List.of(1L, 2L);

        CardInfo card2 = new CardInfo();
        card2.setId(2L);
        card2.setNumber("9876543210987654");
        card2.setExpirationDate(LocalDate.of(26, 6, 30));
        card2.setUser(userEntity);
        card2.setHolder("JOHN DOE");

        CardInfoResponseDto cardResponseDto2 = new CardInfoResponseDto();
        cardResponseDto2.setId(2L);
        cardResponseDto2.setNumber("9876543210987654");
        cardResponseDto2.setExpirationDate(LocalDate.of(26, 6, 30));
        cardResponseDto2.setHolder("JOHN DOE");

        List<CardInfo> cards = List.of(cardEntity, card2);

        when(cardRepository.findAllById(ids)).thenReturn(cards);
        when(cardInfoMapper.toDto(cardEntity)).thenReturn(cardResponseDto);
        when(cardInfoMapper.toDto(card2)).thenReturn(cardResponseDto2);

        List<CardInfoResponseDto> result = cardService.getCardsByIds(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(cardResponseDto.getNumber(), result.get(0).getNumber());
        assertEquals(cardResponseDto2.getNumber(), result.get(1).getNumber());

        verify(cardRepository).findAllById(ids);
        verify(cardInfoMapper, times(2)).toDto(any(CardInfo.class));
    }

    @Test
    void testUpdateCardById() {
        Long cardId = 1L;
        CardInfoRequestDto cardUpdate = new CardInfoRequestDto();
        cardUpdate.setExpirationDate(LocalDate.of(30, 1, 31));

        CardInfo updatedCardEntity = new CardInfo();
        updatedCardEntity.setId(cardId);
        updatedCardEntity.setExpirationDate(cardUpdate.getExpirationDate());

        CardInfoResponseDto updatedCardResponseDto = new CardInfoResponseDto();
        updatedCardResponseDto.setId(cardId);
        updatedCardResponseDto.setExpirationDate(cardUpdate.getExpirationDate());

        when(cardRepository.getCardById(cardId)).thenReturn(cardEntity);
        when(cardRepository.save(cardEntity)).thenReturn(cardEntity);

        when(cardInfoMapper.toDto(cardEntity)).thenReturn(updatedCardResponseDto);

        CardInfoResponseDto result = cardService.updateCardById(cardId, cardUpdate);

        assertNotNull(result);
        assertEquals(updatedCardResponseDto.getExpirationDate(), result.getExpirationDate());
        assertEquals(cardUpdate.getExpirationDate(), cardEntity.getExpirationDate());

        verify(cardRepository).getCardById(cardId);
        verify(cardRepository).save(cardEntity);
        verify(cardInfoMapper).toDto(cardEntity);
    }

    @Test
    void testDeleteCardById() {
        Long cardId = 1L;

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(cardEntity));

        userEntity.getCardList().add(cardEntity);

        cardService.deleteCardById(cardId);

        assertFalse(userEntity.getCardList().contains(cardEntity));

        verify(cardRepository).findById(cardId);
        verify(cardRepository).delete(cardEntity);
    }
}
