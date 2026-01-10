package com.mi.app.repository;

import com.framework.annotations.Repository;
import com.framework.exception.RouteNotFoundException;
import com.mi.app.model.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements IUserRepository {

    private static List<UserEntity> users;

    public UserRepositoryImpl() {
        users = new ArrayList<>();
        users.add(new UserEntity("Alice", "alicia@gmail.com" ,30));
        users.add(new UserEntity("Agustin", "agustin@gmail.com" ,33));
        users.add(new UserEntity("Alan", "alan@gmail.com" ,22));
        users.add(new UserEntity("Bob", "bob@gmail.com" ,25));
        users.add(new UserEntity("Charlie", "charlie@gmail.com" ,35));
        users.add(new UserEntity("Carlos", "carlos@gmail.com" ,23));
    }
    @Override
    public UserEntity getUser(int id) {

        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RouteNotFoundException("User not found"));
    }

    @Override
    public UserEntity createUser(UserEntity user) {

        users.add(user);
        UserEntity createdUser = getUser(user.getId());

        System.out.println(createdUser.toString());

        return createdUser;
    }

    @Override
    public List<UserEntity> listUsers() {
        return users;
    }

    @Override
    public List<UserEntity> listUserByFirtsChar(String name) {
        return users.stream().filter
                (user -> user.getName().charAt(0) == name.charAt(0))
                .collect(Collectors.toList());
    }
}
