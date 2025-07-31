package com.innowise.user_service.userService;

import com.innowise.user_service.userService.dto.user.UserRequestDto;
import com.innowise.user_service.userService.dto.user.UserResponseDto;
import com.innowise.user_service.userService.entity.User;
import com.innowise.user_service.userService.mapper.UserMapper;
import com.innowise.user_service.userService.repository.CardRepository;
import com.innowise.user_service.userService.repository.UserRepository;
import com.innowise.user_service.userService.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    private UserRequestDto userRequestDto;
    private User userEntity;
    private UserResponseDto userResponseDto;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("test@example.com");
        userRequestDto.setName("John");
        userRequestDto.setSurname("Doe");

        userEntity = new User();
        userEntity.setEmail("test@example.com");
        userEntity.setName("John");
        userEntity.setSurname("Doe");

        userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("test@example.com");
        userResponseDto.setName("John");
        userResponseDto.setSurname("Doe");
        userResponseDto.setId(1L);
    }

    @Test
    void testCreateUser() {
        when(userRepository.existsByEmail(userRequestDto.getEmail()))
                .thenReturn(false);
        when(userMapper.toEntity(userRequestDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.createUser(userRequestDto);

        assertEquals(userResponseDto.getEmail(), result.getEmail());
        assertEquals(userResponseDto.getName(), result.getName());
        assertEquals(userResponseDto.getSurname(), result.getSurname());

        verify(userRepository).existsByEmail(userRequestDto.getEmail());
        verify(userMapper).toEntity(userRequestDto);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDTO(userEntity);
    }
    @Test
    void testGetUserById() {
        when(userRepository.getUserById(1L)).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(userResponseDto.getEmail(), result.getEmail());
        assertEquals(userResponseDto.getName(), result.getName());
        assertEquals(userResponseDto.getSurname(), result.getSurname());
    }
    @Test
    void testGetUsers() {
        List<Long> ids = List.of(1L, 2L);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("second@example.com");
        user2.setName("Second");
        user2.setSurname("User");

        List<User> users = List.of(userEntity, user2);

        UserResponseDto userResponseDto2 = new UserResponseDto();
        userResponseDto2.setId(2L);
        userResponseDto2.setEmail("second@example.com");
        userResponseDto2.setName("Second");
        userResponseDto2.setSurname("User");

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(userMapper.toDTO(userEntity)).thenReturn(userResponseDto);
        when(userMapper.toDTO(user2)).thenReturn(userResponseDto2);

        List<UserResponseDto> result = userService.getUsers(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userResponseDto.getEmail(), result.get(0).getEmail());
        assertEquals(userResponseDto2.getEmail(), result.get(1).getEmail());
    }
    @Test
    void testGetUserByEmail() {
        when(userRepository.getUserByEmail("test@example.com")).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(userResponseDto.getEmail(), result.getEmail());
        assertEquals(userResponseDto.getName(), result.getName());
        assertEquals(userResponseDto.getSurname(), result.getSurname());
    }
    @Test
    void testUpdateUser() {
        Long userId = 1L;
        UserRequestDto userUpdate = new UserRequestDto();
        userUpdate.setEmail("updated@example.com");
        userUpdate.setName("UpdatedName");
        userUpdate.setSurname("UpdatedSurname");

        UserResponseDto updatedUserResponseDto = new UserResponseDto();
        updatedUserResponseDto.setId(userId);
        updatedUserResponseDto.setEmail(userUpdate.getEmail());
        updatedUserResponseDto.setName(userUpdate.getName());
        updatedUserResponseDto.setSurname(userUpdate.getSurname());

when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(userEntity));
when(userRepository.existsByEmailAndIdNot(userUpdate.getEmail(), userId)).thenReturn(false);
//when(userEntity.setEmail(userRequestDto.getEmail())).thenReturn()
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDTO(userEntity)).thenReturn(updatedUserResponseDto);

UserResponseDto result = userService.updateUser(userId, userUpdate);

        assertEquals(userUpdate.getEmail(), userEntity.getEmail());
        assertEquals(userUpdate.getName(), userEntity.getName());
        assertEquals(userUpdate.getSurname(), userEntity.getSurname());

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmailAndIdNot(userUpdate.getEmail(), userId);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDTO(userEntity);

    }

    @Test
    void testDeleteUserById() {
        Long userId = 1L;
when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(userEntity));

        userService.deleteUserById(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(userEntity);

    }
}
