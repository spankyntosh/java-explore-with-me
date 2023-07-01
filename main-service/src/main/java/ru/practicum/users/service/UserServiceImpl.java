package ru.practicum.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;
import static ru.practicum.users.mapper.UserMapper.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(NewUserRequest user) {
        return modelToUserDto(userRepository.save(newUserRequestToModel(user)));
    }

    @Override
    public Collection<UserDto> getUsers(Integer from, Integer size, List<Integer> ids) {
        PageRequest request = PageRequest.of(from / size, size);
        if (isNull(ids) || ids.isEmpty()) {
            return modelToUserDtos(userRepository.findAll(request).toList());
        } else {
         return modelToUserDtos(new ArrayList<>(userRepository.findAllByIdIn(ids)));
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
