package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.mapping.UserMapping;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Service
@Getter
public class UserService {

    private final UserRepository userRepository;
    private final UserMapping userMapping;

    public UserService(UserRepository userRepository, UserMapping userMapping) {
        this.userRepository = userRepository;
        this.userMapping = userMapping;
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));
        return userMapping.userDto(user);

    }

    @Transactional
    public UserDto create(User user) {
        Optional<User> chekUser = userRepository.findByEmail(user.getEmail());
        if (chekUser.isPresent()) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        user.setAge(Period.between(user.getBirthday(), LocalDate.now()).getYears());
        User createdUser = userRepository.save(user);
        return userMapping.userDto(createdUser);
    }

    public void delete(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalStateException("User with id " + id + " does not exist");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    // Если помечать метод аннотацией @Transactional, то все операции с данными внутри метода выполняются атомарно.
    // Прервется одна - прервутся все. При этом можно убрать метод save()
    public void update(Long id, String name, String email) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("User with id " + id + " does not exist");
        }
        User user = userOptional.get();
        if (nonNull(user.getName()) && !user.getName().equals(name)) {
            user.setName(name);
        }
        if (nonNull(email) && !user.getEmail().equals(email)) {
            Optional<User> foundByName = userRepository.findByEmail(email);
            if (foundByName.isPresent()) {
                throw new IllegalStateException("User with email " + user.getEmail() + " already exists");
            }
            user.setEmail(email);
        }
        //userRepository.save(user);
    }
}
