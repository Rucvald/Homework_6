package com.example.demo.dto.mapping;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapping {
    UserDto userDto(User user);
}
