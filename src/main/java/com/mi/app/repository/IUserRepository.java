package com.mi.app.repository;

import com.mi.app.model.UserEntity;

import java.util.List;

public interface IUserRepository {
    UserEntity getUser(int id);
    UserEntity createUser(UserEntity user);
    List<UserEntity> listUsers();

    List<UserEntity> listUserByFirtsChar(String name);
}
