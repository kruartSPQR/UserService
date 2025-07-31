package com.innowise.user_service.userService.mapper;

import com.innowise.user_service.userService.dto.user.UserRequestDto;
import com.innowise.user_service.userService.dto.user.UserResponseDto;

import com.innowise.user_service.userService.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CardInfoMapper.class)
public interface UserMapper {

    @Mapping(source = "cardList", target = "cardList")
    UserResponseDto toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cardList",  ignore = true)
    User toEntity(UserRequestDto userDTO);

}
