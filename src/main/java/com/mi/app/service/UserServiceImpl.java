package com.mi.app.service;

import com.framework.annotations.Component;
import com.framework.annotations.PostConstruct;
import com.framework.annotations.Service;
import com.framework.annotations.Value;
import com.mi.app.controller.dto.UserDTO;
import com.mi.app.repository.IUserRepository;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final String appName;

    public UserServiceImpl(IUserRepository userRepository, @Value("app.nombre") String appName) {
        this.userRepository = userRepository;
        this.appName = appName;
    }
    @PostConstruct
    public void init() {


        if (appName == null || appName.isEmpty()) {
            throw new RuntimeException("El nombre de la app no se cargó correctamente");
        }


    }
    @Override
    public UserDTO getUserInfo(UserDTO userDTO) {

        return userRepository.getUser(userDTO);
    }
}
