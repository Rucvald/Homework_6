package com.example.demo.dto.mapping;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapping {
    @Mapping(source = "birthday", target = "birthday")
    UserDto userDto(User user);
    @Mapping(source = "birthday", target = "birthday")
    User user(UserDto userDto);
}
