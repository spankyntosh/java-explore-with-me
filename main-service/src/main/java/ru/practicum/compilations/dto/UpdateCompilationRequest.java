package ru.practicum.compilations.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class UpdateCompilationRequest {

    @Size(min = 1, max = 50, message = "Длина заголовка подборки должна быть больше 1 символа и меньше 50")
    private String title;
    private Boolean pinned;
    private List<Integer> events;
}
