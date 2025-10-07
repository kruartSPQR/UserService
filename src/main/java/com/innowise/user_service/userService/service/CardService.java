package com.innowise.user_service.userService.service;

import com.innowise.common.exception.DuplicateResourceCustomException;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import com.innowise.user_service.userService.dto.card.CardInfoRequestDto;
import com.innowise.user_service.userService.dto.card.CardInfoResponseDto;
import com.innowise.user_service.userService.entity.CardInfo;
import com.innowise.user_service.userService.entity.User;
import com.innowise.user_service.userService.mapper.CardInfoMapper;
import com.innowise.user_service.userService.repository.CardRepository;
import com.innowise.user_service.userService.repository.UserRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;
    private final CacheManager cacheManager;

    public CardInfoResponseDto createCard(CardInfoRequestDto cardInfoRequestDto) {

        User user = userRepository.getUserById(cardInfoRequestDto.getUserId());
        if (user == null) {
            throw new ResourceNotFoundCustomException("Can't create a card without a user");
        }
        if (cardRepository.existsByNumber(cardInfoRequestDto.getNumber())) {
            throw new DuplicateResourceCustomException("Card number already exists");
        }

        CardInfo cardInfoEntity = cardInfoMapper.toEntity(cardInfoRequestDto);
        cardInfoEntity.setUser(user);
        cardInfoEntity.setHolder((user.getName() + " " + user.getSurname()).toUpperCase());

        user.getCardList().add(cardInfoEntity);
//        userRepository.save(user);
        cardRepository.save(cardInfoEntity);
        evictUserCache(user.getId());
        return cardInfoMapper.toDto(cardInfoEntity);
    }

    @Transactional(readOnly = true)
    public CardInfoResponseDto getCardById(Long id) {

        CardInfo card = cardRepository.getCardById(id);
        if (card == null) {
            throw new ResourceNotFoundCustomException("Card with id " + id + " not found");
        }

        return cardInfoMapper.toDto(card);
    }

    @Transactional(readOnly = true)
    public List<CardInfoResponseDto> getCardsByIds(List<Long> ids) {

        List<CardInfo> list = cardRepository.findAllById(ids);

        return list.stream().map(cardEntity -> cardInfoMapper.toDto(cardEntity)).toList();
    }

    public CardInfoResponseDto updateCardById(Long id, CardInfoRequestDto cardInfo) {

        CardInfo card = cardRepository.getCardById(id);
        if (card == null) {
            throw new ResourceNotFoundCustomException("Card with id " + id + " not found");
        }

        card.setExpirationDate(cardInfo.getExpirationDate());
        cardRepository.save(card);

        return cardInfoMapper.toDto(card);
    }

    public void deleteCardById(Long id) {

        CardInfo card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundCustomException("Card with id " + id + " not found"));
        cardRepository.delete(card);

        User user = card.getUser();
        if (user != null) {
            user.getCardList().remove(card);
//            userRepository.save(user);
            evictUserCache(user.getId());
        }

    }
    private void evictUserCache(Long userId) {
        Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            cache.evict(userId);
        }
    }
}
