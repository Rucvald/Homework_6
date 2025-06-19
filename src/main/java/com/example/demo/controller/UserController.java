package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/users")
@Tag(name = "user-controller")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Find and return entity by ID",
            description = "Finds entity with the specified ID in the database and returns this entity",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Entity found and returned"),
                    @ApiResponse(responseCode = "404", description = "Entity with specified ID not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied")
            })
    public UserDto findById(@Parameter(description = "ID of the entity to be found and retrieved", required = true, example = "1")
                            @RequestParam Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @Operation(summary = "Add new entity",
            description = "Creates a new entity with the provided data and returns the created user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Entity successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    @RequestBody(
            description = "User data to create",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class),
                    examples = @ExampleObject(
                            name = "UserCreateExample",
                            summary = "Example user creation",
                            value = "{ \"name\": \"Vasya Pupkin\", \"email\": \"pupkin@example.com\", \"birthday\": \"2000-06-19\" }"
                    )
            )
    )
    public UserDto create(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @DeleteMapping(path = "{id}")
    @Operation(summary = "Delete entity by ID",
            description = "Deletes entity with the specified ID from the database",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Entity successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Entity with specified ID not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid ID supplied")
            })
    public void delete(@Parameter(description = "ID of the entity to be found and deleted", required = true, example = "1")
                       @PathVariable(name = "id") Long id) {
        userService.delete(id);
    }

    @PutMapping(path = "{id}")
    @Operation(summary = "Update entity by ID",
            description = "Updates name and email of the provided data, if provided, about entity with specified ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Entity successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Entity with specified ID not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data or ID supplied")
            })
    public void update(@Parameter(description = "ID of the entity to update", required = true, example = "1")
                       @PathVariable(name = "id") Long id,
                       @Parameter(description = "New name of the entity", example = "Vasya Pupkin")
                       @RequestParam(required = false) String name,
                       @Parameter(description = "New email of the entity", example = "pupkin@example.com")
                       @RequestParam(required = false) String email) {
        userService.update(id, name, email);
    }
}
