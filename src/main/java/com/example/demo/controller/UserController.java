package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserDto findById(@RequestParam Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public UserDto create(@RequestBody User user) {
        return userService.create(user);
    }

    @DeleteMapping(path = "{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        userService.delete(id);
    }

    @PutMapping(path = "{id}")
    public void update(@PathVariable(name = "id") Long id,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String email) {
        userService.update(id, name, email);
    }
}
