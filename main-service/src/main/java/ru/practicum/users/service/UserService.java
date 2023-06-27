package ru.practicum.users.service;

import ru.practicum.users.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    User createUser(User user);
    Collection<User> getUsers(Integer from, Integer size, List<Integer> ids);
    void deleteUser(Integer userId);
}
