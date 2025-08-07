package com.innowise.user_service.userService.mapper;

import com.innowise.user_service.userService.dto.card.CardInfoRequestDto;
import com.innowise.user_service.userService.dto.card.CardInfoResponseDto;
import com.innowise.user_service.userService.entity.CardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    @Mapping(source = "user.id", target = "userId")
    CardInfoResponseDto toDto(CardInfo cardInfo);

    @Mapping(target = "user", ignore = true)
    CardInfo toEntity(CardInfoRequestDto cardInfoDto);

}
