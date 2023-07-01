package ru.practicum.categories.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CategoryDto {
    private int id;
    private String name;
}
