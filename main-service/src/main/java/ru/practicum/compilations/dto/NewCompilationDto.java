package ru.practicum.compilations.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class NewCompilationDto {

    @NotBlank
    @Size(min = 1, max = 50, message = "Длина заголовка подборки должна быть больше 1 символа и меньше 50")
    private String title;
    private Collection<Integer> events;
    private boolean pinned;
}
