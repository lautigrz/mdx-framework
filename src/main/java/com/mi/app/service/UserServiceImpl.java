package com.mi.app.service;

import com.framework.annotations.Service;
import com.mi.app.controller.dto.CreateUserDTO;
import com.mi.app.controller.dto.UserDTO;
import com.mi.app.model.UserEntity;
import com.mi.app.repository.IUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;

    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO getUser(int id) {
        UserEntity user = userRepository.getUser(id);
        return new UserDTO(user.getName(), user.getEmail(), user.getAge());
    }

    @Override
    public CreateUserDTO createUser(UserDTO userDTO) {

        UserEntity userEntity = new UserEntity(userDTO.getName(), userDTO.getEmail(), userDTO.getAge());
        userEntity= userRepository.createUser(userEntity);

        return new CreateUserDTO(userEntity.getName(), userEntity.getEmail(), "Usuario creado con id: " + userEntity.getId());
    }

    @Override
    public List<UserDTO> listUsers() {

        List<UserEntity> users = userRepository.listUsers();

        return users.stream().map(u ->
                        new UserDTO(u.getName(), u.getEmail(), u.getAge()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getUsersByQuery(String name) {

        List<UserEntity> users = userRepository.listUserByFirtsChar(name);

        return users.stream()
                .map(u -> new UserDTO(u.getName(), u.getEmail(), u.getAge()))
                .collect(Collectors.toList());
    }
}
