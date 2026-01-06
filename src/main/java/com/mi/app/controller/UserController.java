package com.mi.app.controller;

import com.framework.annotations.*;
import com.mi.app.controller.dto.UserDTO;

import java.util.List;

@RestResource
public class UserController {

    @Get("/user")
    public String getUserInfo(@QueryParam("name") String name, @QueryParam("age") int age) {
        return "User " + name + " is " + age + " years old.";
    }

    @Get("/user/list/{id}")
    public String listUsers(@PathParam("id") int id, @QueryParam("name") String name) {
        return "User List " + id + ": " + name;
    }

    @Post("/user/new")
    public List<UserDTO>  newUser(@FromBody List<UserDTO> users) {
        Object primerElemento = users.get(0);
        System.out.println("Clase real: " + primerElemento.getClass().getName());
        return users;
    }
}
