package com.mi.app.controller;

import com.framework.annotations.*;

@RestController
public class UserController {


    @GetMapping("/user")
    public String getUserInfo(@RequestParam("name") String name, @RequestParam("age") int age) {
        return "User " + name + " is " + age + " years old.";
    }

    @GetMapping("/user/list/{id}")
    public String listUsers(@PathVariable("id") int id, @RequestParam("name") String name) {
        return "User List " + id + ": " + name;
    }

    @PostMapping("/user/new")
    public String newUser(){
        return "New User Created";
    }
}
