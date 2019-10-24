package ru.xfneo.testgis;

import ru.xfneo.testgis.dao.UserDao;
import ru.xfneo.testgis.dao.UserDaoImpl;
import ru.xfneo.testgis.model.User;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();
        Optional<User> userCandidate = userDao.find("Maksim");
        User user = null;
        if (userCandidate.isPresent()){
            user = userCandidate.get();
        }
        System.out.println(user);

        if (user != null){
            user.setLastName("New Last Name");
        }
        userDao.update(user);
        Optional<User> updatedUserCandidate = userDao.find("Maksim");
        User updatedUser = null;
        if (updatedUserCandidate.isPresent()){
            updatedUser = updatedUserCandidate.get();
        }
        System.out.println(updatedUser);
    }
}
