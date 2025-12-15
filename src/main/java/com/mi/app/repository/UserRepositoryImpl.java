package com.mi.app.repository;

import com.framework.annotations.Component;

@Component
public class UserRepositoryImpl implements IUserRepository {

    @Override
    public String getUser() {
        return "Usuario desde el repositorio";
    }
}
