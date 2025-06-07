package com.example.demo;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.mapping.UserMapping;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserMapping userMapping;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapping = mock(UserMapping.class);
        userService = new UserService(userRepository, userMapping);
    }

    @Test
    void createUser_whenEmailNotExists_shouldSaveAndReturnDto() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setEmail("test@example.com");
        savedUser.setBirthday(user.getBirthday());
        savedUser.setAge(33); // предположим текущий год 2023
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto userDto = new UserDto();
        when(userMapping.userDto(savedUser)).thenReturn(userDto);

        UserDto result = userService.create(user);

        assertNotNull(result);
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
        verify(userMapping).userDto(savedUser);
    }

    @Test
    void createUser_whenEmailExists_shouldThrow() {
        User user = new User();
        user.setEmail("exist@example.com");

        when(userRepository.findByEmail("exist@example.com")).thenReturn(Optional.of(new User()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.create(user));

        assertEquals("User with email exist@example.com already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findById_whenUserExists_shouldReturnDto() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto userDto = new UserDto();
        when(userMapping.userDto(user)).thenReturn(userDto);

        UserDto result = userService.findById(1L);

        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userMapping).userDto(user);
    }

    @Test
    void findById_whenUserNotFound_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.findById(1L));

        assertEquals("User with id 1 not found", ex.getMessage());
    }

    @Test
    void delete_whenUserExists_shouldDelete() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_whenUserNotFound_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.delete(1L));

        assertEquals("User with id 1 does not exist", ex.getMessage());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void update_whenUserExists_shouldUpdateFields() {
        User user = new User();
        user.setId(1L);
        user.setName("OldName");
        user.setEmail("old@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        userService.update(1L, "NewName", "new@example.com");

        assertEquals("NewName", user.getName());
        assertEquals("new@example.com", user.getEmail());
        // save не вызывается, т.к. метод помечен @Transactional
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_whenUserNotFound_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.update(1L, "Name", "email@example.com"));

        assertEquals("User with id 1 does not exist", ex.getMessage());
    }

    @Test
    void update_whenEmailExists_shouldThrow() {
        User user = new User();
        user.setId(1L);
        user.setEmail("old@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("exist@example.com")).thenReturn(Optional.of(new User()));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.update(1L, null, "exist@example.com"));

        assertEquals("User with email old@example.com already exists", ex.getMessage());
    }
}
