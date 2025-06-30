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

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
@Getter
public class UserService {

    private final UserRepository userRepository;
    private final UserMapping userMapping;
    private final ProducerService producerService;

    private static final String PRODUCER_SERVICE_CB = "producerServiceCircuitBreaker";

    public UserService(UserRepository userRepository, UserMapping userMapping, ProducerService producerService) {
        this.userRepository = userRepository;
        this.userMapping = userMapping;
        this.producerService = producerService;
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));
        return userMapping.userDto(user);
    }

    @Transactional
    public UserDto create(UserDto userDto) {
        Optional<User> chekUser = userRepository.findByEmail(userDto.getEmail());
        if (chekUser.isPresent()) {
            throw new IllegalArgumentException("User with email " + userDto.getEmail() + " already exists");
        }
        User userEntity = userMapping.user(userDto);
        userEntity.setAge(Period.between(userEntity.getBirthday(), LocalDate.now()).getYears());
        User createdUser = userRepository.save(userEntity);

        sendMessageForCreateWithCB(createdUser);

        return userMapping.userDto(createdUser);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("User with id " + id + " not found"));
        sendMessageForDeleteWithCB(user);
        userRepository.deleteById(id);
    }

    @Transactional
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
    }

    @CircuitBreaker(name = PRODUCER_SERVICE_CB, fallbackMethod = "fallbackSendMessageForCreate")
    public void sendMessageForCreateWithCB(User user) {
        producerService.sendMessageForCreate(user);
    }

    @CircuitBreaker(name = PRODUCER_SERVICE_CB, fallbackMethod = "fallbackSendMessageForDelete")
    public void sendMessageForDeleteWithCB(User user) {
        producerService.sendMessageForDelete(user);
    }

    public void fallbackSendMessageForCreate(User user, Throwable t) {
        System.err.println("Fallback: sendMessageForCreate failed for user " + user.getId() + ", reason: " + t.getMessage());
    }

    public void fallbackSendMessageForDelete(User user, Throwable t) {
        System.err.println("Fallback: sendMessageForDelete failed for user " + user.getId() + ", reason: " + t.getMessage());
    }
}

