package tw.com.pluto.service;

import tw.com.pluto.model.User;

import java.util.List;

public interface UserService {
    User save(User user);
    User getUser(Integer id);
    List<User> getUsers();
    void deleteUser(Integer id);
}