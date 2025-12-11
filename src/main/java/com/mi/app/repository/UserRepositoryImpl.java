package com.mi.app.repository;

public class UserRepositoryImpl implements IUserRepository {

    @Override
    public String getUserById() {
        return "Usuario desde el repositorio";
    }
}
