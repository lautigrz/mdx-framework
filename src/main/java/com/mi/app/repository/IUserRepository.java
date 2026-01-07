package com.mi.app.repository;

import com.mi.app.controller.dto.UserDTO;

public interface IUserRepository {
    UserDTO getUser(UserDTO userDTO);

}
