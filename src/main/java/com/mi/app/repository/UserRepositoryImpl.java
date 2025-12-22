package com.mi.app.repository;

import com.framework.annotations.Component;
import com.framework.annotations.Repository;

@Repository
public class UserRepositoryImpl implements IUserRepository {

    @Override
    public String getUser() {
        return "Usuario desde el repositorio";
    }
}
