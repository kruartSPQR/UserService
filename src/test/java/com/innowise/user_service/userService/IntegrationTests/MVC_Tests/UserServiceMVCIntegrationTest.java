package com.innowise.user_service.userService.IntegrationTests.MVC_Tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.user_service.userService.controller.UserController;
import com.innowise.user_service.userService.dto.user.UserRequestDto;
import com.innowise.user_service.userService.dto.user.UserResponseDto;
import com.innowise.user_service.userService.service.UserService;
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

@WebMvcTest(UserController.class)
public class UserServiceMVCIntegrationTest{

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

        private UserRequestDto getTestUserRequestDto() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("Walter");
        userRequestDto.setSurname("White");
        userRequestDto.setEmail("walter.white@example.com");
        userRequestDto.setBirthDate(LocalDate.of(1990, 1, 1));

        return userRequestDto;
    }
        private List<UserResponseDto> getTestUserDtos() {
        UserResponseDto user1 = new UserResponseDto();
        user1.setName("John");
        user1.setSurname("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setBirthDate(LocalDate.of(1990, 1, 1));

        UserResponseDto user2 = new UserResponseDto();
        user2.setName("Mike");
        user2.setSurname("Jordan");
        user2.setEmail("mike.jordan@example.com");
        user2.setBirthDate(LocalDate.of(1992, 3, 4));
        return List.of(user1, user2);
    }
UserResponseDto getUpdatedResponse(){
    UserResponseDto user2 = new UserResponseDto();
    user2.setName("Walter");
    user2.setSurname("White");
    user2.setEmail("walter.white@example.com");
    user2.setBirthDate(LocalDate.of(1992, 3, 4));
    return user2;
}
    @Test
    void shouldCreateUser() throws Exception {

        mockMvc.perform(post("/api/v1/users/add")
        .content(objectMapper.writeValueAsString(getTestUserRequestDto()))
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isCreated());
    }
    @Test
    void shouldUpdateUser() throws Exception {
        Mockito.when(userService.updateUser(1L, getTestUserRequestDto()))
                .thenReturn(getUpdatedResponse());

        mockMvc.perform(put("/api/v1/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getTestUserRequestDto())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Walter"))
                .andExpect(jsonPath("$.surname").value("White"))
                .andExpect(jsonPath("$.email").value("walter.white@example.com"));

    }
    @Test
    void shouldGetUserById() throws Exception {

        Mockito.when(userService.getUserById(2L)).thenReturn(getTestUserDtos().get(1));
        mockMvc
                .perform(get("/api/v1/users/{user_id}", "2"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Mike"))
                .andExpect(jsonPath("$.email").value("mike.jordan@example.com"));
    }
    @Test
    void shouldGetUserByEmail() throws Exception {

        Mockito.when(userService.getUserByEmail("mike.jordan@example.com")).thenReturn(getTestUserDtos().get(1));
        mockMvc
                .perform(get("/api/v1/users/email/{email}", "mike.jordan@example.com"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Mike"))
                .andExpect(jsonPath("$.email").value("mike.jordan@example.com"));
    }
    @Test
    void shouldGetUsersByIds() throws Exception {

        Mockito.when(userService.getUsers(List.of(1L,2L))).thenReturn(getTestUserDtos());

        mockMvc
                .perform(get("/api/v1/users").param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[1].name").value("Mike"))
                .andExpect(jsonPath("$[1].email").value("mike.jordan@example.com"));
    }
    @Test
    void shouldDeleteUserById() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(null);
        mockMvc.perform(delete("/api/v1/users/{id}", "1"))
                .andExpect(status().isNoContent());
    }
}
