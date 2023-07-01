package ru.practicum.users.service;

import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest user);
    Collection<UserDto> getUsers(Integer from, Integer size, List<Integer> ids);
    void deleteUser(Integer userId);
}
