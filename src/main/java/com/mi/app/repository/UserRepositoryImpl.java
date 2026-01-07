package com.mi.app.repository;

import com.framework.annotations.Component;
import com.framework.annotations.Repository;
import com.mi.app.controller.dto.UserDTO;

@Repository
public class UserRepositoryImpl implements IUserRepository {

    @Override
    public UserDTO getUser(UserDTO userDTO) {
        return userDTO;
    }
}
