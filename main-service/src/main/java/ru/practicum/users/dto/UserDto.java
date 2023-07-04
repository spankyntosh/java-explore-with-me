package ru.practicum.users.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class UserDto {

    private Integer id;
    private String name;
    private String email;
}
