package com.innowise.user_service.userService.IntegrationTests;

import com.innowise.user_service.userService.dto.user.UserRequestDto;
import com.innowise.user_service.userService.dto.user.UserResponseDto;
import com.innowise.user_service.userService.exception.DuplicateResourceCustomException;
import com.innowise.user_service.userService.exception.ResourceNotFoundCustomException;
import com.innowise.user_service.userService.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    private UserRequestDto createTestUserDto() {
        UserRequestDto dto = new UserRequestDto();
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setBirthDate(LocalDate.of(1990, 1, 1));
        return dto;
    }

    @Test
    void shouldCreateUser() {
        UserRequestDto request = createTestUserDto();
        UserResponseDto response = userService.createUser(request);

        assertNotNull(response.getId());
        assertEquals("John", response.getName());

        UserResponseDto byId = userService.getUserById(response.getId());
        assertEquals(response.getEmail(), byId.getEmail());

        UserResponseDto byEmail = userService.getUserByEmail(response.getEmail());
        assertEquals(response.getId(), byEmail.getId());
    }

    @Test
    void shouldUpdateUser() {
        UserResponseDto created = userService.createUser(createTestUserDto());

        UserRequestDto updateRequest = createTestUserDto();
        updateRequest.setName("UpdatedJohn");
        updateRequest.setEmail("updated@example.com");

        UserResponseDto updated = userService.updateUser(created.getId(), updateRequest);

        assertEquals("UpdatedJohn", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    void shouldDeleteUser() {
        UserResponseDto created = userService.createUser(createTestUserDto());
        userService.deleteUserById(created.getId());

        assertThrows(ResourceNotFoundCustomException.class,
                () -> userService.getUserById(created.getId()));
    }

    @Test
    void shouldGetUsers() {
        UserRequestDto user1 = createTestUserDto();
        user1.setEmail("user1@example.com");

        UserRequestDto user2 = createTestUserDto();
        user2.setEmail("user2@example.com");

        UserResponseDto created1 = userService.createUser(user1);
        UserResponseDto created2 = userService.createUser(user2);

        List<UserResponseDto> users = userService.getUsers(List.of(created1.getId(), created2.getId()));

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("user1@example.com")));
        assertTrue(users.stream().anyMatch(u -> u.getEmail().equals("user2@example.com")));
    }

    @Test
    void shouldPreventDuplicateEmailWhenCreatingNewUser() {
        userService.createUser(createTestUserDto());

        assertThrows(DuplicateResourceCustomException.class,
                () -> userService.createUser(createTestUserDto()));
    }

    @Test
    void shouldPreventDuplicateEmailWHileUserUpdating() {
        UserResponseDto user1 = userService.createUser(createTestUserDto());

        UserRequestDto user2Request = createTestUserDto();
        user2Request.setEmail("second@example.com");
        UserResponseDto user2 = userService.createUser(user2Request);

        UserRequestDto updateRequest = createTestUserDto();
        updateRequest.setEmail("second@example.com");

        assertThrows(DuplicateResourceCustomException.class,
                () -> userService.updateUser(user1.getId(), updateRequest));
    }

    @Test
    void shouldCachingUserData() {
        UserResponseDto created = userService.createUser(createTestUserDto());

        userService.getUserById(created.getId());

        Cache cache = cacheManager.getCache("users");
        assertNotNull(cache);
        assertNotNull(cache.get(created.getId()));

        UserRequestDto updateRequest = createTestUserDto();
        updateRequest.setName("CachedUpdate");
        userService.updateUser(created.getId(), updateRequest);

        assertEquals(updateRequest.getName(), ((UserResponseDto) cache.get(created.getId()).get()).getName());
    }

    @Test
    void shouldEvictCacheWhenDelete() {
        UserResponseDto created = userService.createUser(createTestUserDto());
        userService.getUserById(created.getId());

        Cache cache = cacheManager.getCache("users");
        assertNotNull(cache.get(created.getId()));

        userService.deleteUserById(created.getId());
        assertNull(cache.get(created.getId()));
    }
}
