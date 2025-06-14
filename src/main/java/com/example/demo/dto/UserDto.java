package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    private String name;
    private int age;
    private String email;
    private LocalDate birthday;
}
