package ru.practicum.users.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class UserShortDto {
    private int id;
    private String name;
}
