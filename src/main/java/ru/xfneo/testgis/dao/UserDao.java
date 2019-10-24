package ru.xfneo.testgis.dao;

import ru.xfneo.testgis.model.User;

import java.util.Optional;

public interface UserDao {
    void update(User user);
    Optional<User> find(String name);
}
