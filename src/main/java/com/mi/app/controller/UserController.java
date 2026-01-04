package com.mi.app.controller;

import com.framework.annotations.GetMapping;
import com.framework.annotations.PostMapping;
import com.framework.annotations.RequestParam;
import com.framework.annotations.RestController;

@RestController
public class UserController {


    @GetMapping("/user")
    public String getUserInfo(@RequestParam("name") String name, @RequestParam("age") int age) {
        return "User " + name + " is " + age + " years old.";
    }


    @GetMapping("/user/list")
    public String listUsers() {
        return "User List";
    }

    @PostMapping("/user/new")
    public String newUser(){
        return "New User Created";
    }
}
