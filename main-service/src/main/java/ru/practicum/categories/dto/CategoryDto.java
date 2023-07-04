package ru.practicum.categories.dto;

import lombok.*;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CategoryDto {
    private int id;
    @Size(min = 1, max = 50, message = "Длина названия категории должно быть больше одного символа и меньше 50")
    private String name;
}
