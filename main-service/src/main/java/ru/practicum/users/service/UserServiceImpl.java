package ru.practicum.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Collection<User> getUsers(Integer from, Integer size, List<Integer> ids) {
        PageRequest request = PageRequest.of(from / size, size);
        if (isNull(ids) || ids.isEmpty()) {
            return userRepository.findAll(request).toList();
        } else {
         return new ArrayList<>(userRepository.findAllByIdIn(ids, request));
        }
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с таким id не существует");
        }
        userRepository.deleteById(userId);
    }
}
