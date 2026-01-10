package com.mi.app.service;

import com.mi.app.controller.dto.CreateUserDTO;
import com.mi.app.controller.dto.UserDTO;

import java.util.List;

public interface IUserService {
    UserDTO getUser(int id);
    CreateUserDTO createUser(UserDTO userDTO);
    List<UserDTO> listUsers();

    List<UserDTO>  getUsersByQuery(String name);
}
