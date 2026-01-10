package com.mi.app.controller.dto;

public class UserDTO {

    private String name;
    private String email;
    private int age;

    public UserDTO() {}
    public UserDTO(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
