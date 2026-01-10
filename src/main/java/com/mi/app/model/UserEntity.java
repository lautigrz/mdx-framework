package com.mi.app.model;

public class UserEntity {
    private static int ID_GENERATOR = 0; // contador global

    private final int id;
    private String name;
    private int age;
    private String email;

    public UserEntity(String name,String email, int age) {
        this.id = ++ID_GENERATOR;
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                '}';
    }
}
