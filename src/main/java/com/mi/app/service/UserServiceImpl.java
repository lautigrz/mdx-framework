package com.mi.app.service;

import com.mi.app.repository.IUserRepository;

public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getUserInfo() {
        return userRepository.getUserById();
    }
}
