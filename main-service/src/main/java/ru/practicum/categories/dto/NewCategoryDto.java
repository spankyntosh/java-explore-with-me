package ru.practicum.categories.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class NewCategoryDto {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 50, message = "Длина названия категории должно быть больше одного символа и меньше 50")
    private String name;

    @Override
    public String toString() {
        return String.format("Новая категория с именем %s", this.name);
    }
}
