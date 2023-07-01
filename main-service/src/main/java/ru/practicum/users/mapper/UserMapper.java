package ru.practicum.users.mapper;

import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserShortDto modelToUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static UserDto modelToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User newUserRequestToModel(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public static Collection<UserDto> modelToUserDtos(Collection<User> users) {
        return users.stream()
                .map(UserMapper::modelToUserDto)
                .collect(Collectors.toList());
    }
}
