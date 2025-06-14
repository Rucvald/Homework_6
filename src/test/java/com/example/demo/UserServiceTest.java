package com.example.demo;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProducerService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired

    @MockitoBean
    private ProducerService producerService;

    @Test
    void createUser_whenEmailNotExists_shouldSaveAndReturnDto() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setBirthday(LocalDate.of(1990, 1, 1));
        userDto.setName("Test User");

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals(userDto.getBirthday(), result.getBirthday());

        Optional<User> savedUser = userRepository.findByEmail("test@example.com");
        assertTrue(savedUser.isPresent());
        assertEquals("Test User", savedUser.get().getName());
    }

    @Test
    void createUser_whenEmailExists_shouldThrow() {
        User existing = new User();
        existing.setEmail("exist@example.com");
        existing.setBirthday(LocalDate.of(1990, 1, 1));
        existing.setName("Existing User");
        existing.setAge(Period.between(existing.getBirthday(), LocalDate.now()).getYears());
        userRepository.save(existing);

        UserDto userDto = new UserDto();
        userDto.setEmail("exist@example.com");
        userDto.setBirthday(LocalDate.of(1995, 5, 5));
        userDto.setName("New User");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.create(userDto));

        assertEquals("User with email exist@example.com already exists", ex.getMessage());
    }

    @Test
    void findById_whenUserExists_shouldReturnDto() {
        User user = new User();
        user.setEmail("find@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setName("Find Me");
        User saved = userRepository.save(user);

        UserDto result = userService.findById(saved.getId());

        assertNotNull(result);
    }

    @Test
    void findById_whenUserNotFound_shouldThrow() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.findById(999L));

        assertEquals("User with id 999 not found", ex.getMessage());
    }

    @Test
    void delete_whenUserExists_shouldDelete() {
        User user = new User();
        user.setEmail("delete@example.com");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setName("To Delete");
        User saved = userRepository.save(user);

        userService.delete(saved.getId());

        assertFalse(userRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void delete_whenUserNotFound_shouldThrow() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.delete(123L));

        assertEquals("User with id 123 not found", ex.getMessage());
    }

    @Test
    void update_whenUserExists_shouldUpdateFields() {
        User user = new User();
        user.setEmail("old@example.com");
        user.setName("Old Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User saved = userRepository.save(user);

        userService.update(saved.getId(), "New Name", "new@example.com");

        User updated = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals("New Name", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test
    void update_whenUserNotFound_shouldThrow() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.update(999L, "Name", "email@example.com"));

        assertEquals("User with id 999 does not exist", ex.getMessage());
    }

    @Test
    void update_whenEmailExists_shouldThrow() {
        User user1 = new User();
        user1.setEmail("exist@example.com");
        user1.setName("User1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("old@example.com");
        user2.setName("User2");
        user2.setBirthday(LocalDate.of(1995, 5, 5));
        User saved = userRepository.save(user2);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> userService.update(saved.getId(), null, "exist@example.com"));

        assertEquals("User with email old@example.com already exists", ex.getMessage());
    }
}