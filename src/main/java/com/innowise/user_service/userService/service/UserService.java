package com.innowise.user_service.userService.service;

import com.innowise.user_service.userService.dto.user.UserResponseDto;
import com.innowise.user_service.userService.dto.user.UserRequestDto;
import com.innowise.user_service.userService.entity.User;
import com.innowise.user_service.userService.exception.DuplicateResourceCustomException;
import com.innowise.user_service.userService.exception.ResourceNotFoundCustomException;
import com.innowise.user_service.userService.mapper.UserMapper;
import com.innowise.user_service.userService.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new DuplicateResourceCustomException("Email already exists");
        }

        User userEntity = userMapper.toEntity(userRequestDto);

        userRepository.save(userEntity);

        return userMapper.toDTO(userEntity);
    }
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {

        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundCustomException("User with id: " + id + " not found");
        }

        return userMapper.toDTO(user);
    }
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers(List<Long> ids) {

        List<User> userList = userRepository.findAllById(ids);

        return userList.stream()
                .map(u -> userMapper.toDTO(u))
                .toList();
    }
    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {

        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundCustomException("User with email: " + email + " not found");
        }

        return userMapper.toDTO(user);
    }
    public UserResponseDto updateUser(Long id, UserRequestDto userUpdate) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundCustomException("User with id: " + id + " not found"));

        if(userRepository.existsByEmailAndIdNot(userUpdate.getEmail(), id)) {
        throw new DuplicateResourceCustomException("Email already exists");
        }
        else {
            user.setEmail(userUpdate.getEmail());
        }

        user.setName(userUpdate.getName());
        user.setSurname(userUpdate.getSurname());

        userRepository.save(user);

         return userMapper.toDTO(user);
    }
    public void deleteUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundCustomException("User with id: " + id + " not found"));
        userRepository.delete(user);
    }
}
