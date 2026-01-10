package com.mi.app.controller;

import com.framework.annotations.*;
import com.mi.app.controller.dto.CreateUserDTO;
import com.mi.app.controller.dto.UserDTO;
import com.mi.app.service.IUserService;

import java.util.List;

@RestResource
public class UserController {

    private final IUserService userService;
    public UserController(IUserService userService) {
        this.userService = userService;

    }

    @Get("/user/list")
    public List<UserDTO> getUserInfo() {
        return userService.listUsers();
    }

    @Get("/user/{id}")
    public UserDTO listUsers(@PathParam("id") int id) {
        return userService.getUser(id);
    }

    @Get("/user")
    public List<UserDTO> getUsersByQuery(@QueryParam("name") String name) {
        return userService.getUsersByQuery(name);
    }

    @Post("/user")
    public CreateUserDTO newUser(@FromBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }
}
