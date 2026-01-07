package com.mi.app.controller;

import com.framework.annotations.*;
import com.mi.app.controller.dto.UserDTO;
import com.mi.app.service.IUserService;

import java.util.List;

@RestResource
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Get("/user")
    public String getUserInfo(@QueryParam("name") String name, @QueryParam("age") int age) {
        return "User " + name + " is " + age + " years old.";
    }

    @Get("/user/list/{id}")
    public String listUsers(@PathParam("id") int id, @QueryParam("name") String name) {
        return "User List " + id + ": " + name;
    }

    @Post("/user/new")
    public String newUser(@FromBody UserDTO userDTO) {
        UserDTO user = userService.getUserInfo(userDTO);
        String info = "User Info: " + user.getName() + ", Age: " + user.getAge();
        return info;
    }
}
