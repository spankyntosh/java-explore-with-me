package ru.practicum.users.mapper;

import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.model.User;

public class UserMapper {

    public static UserShortDto modelToUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
