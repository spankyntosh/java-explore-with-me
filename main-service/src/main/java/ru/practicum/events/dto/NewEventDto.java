package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.events.model.Location;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class NewEventDto {
    @NotBlank(message = "Описание не должно быть пустым")
    @Size(min = 20, max = 2000, message = "текст описания должен быть больше 20 и меньше 2000 символов")
    private String annotation;
    @NotNull
    private int category;
    @NotBlank(message = "Описание события не должно быть пустым")
    @Size(min = 20, max = 7000, message = "текст описания события должен быть больше 20 и меньше 7000 символов")
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @FutureOrPresent(message = "дата события не должна быть в прошлом")
    private LocalDateTime eventDate;
    @NotNull
    private Location location;
    private boolean paid = false;
    @PositiveOrZero
    private int participantLimit = 0;
    private boolean requestModeration = true;
    @NotBlank
    @Size(min = 3, max = 120, message = "текст заголовка должен быть больше 3 и меньше 120 символов")
    private String title;

    @Override
    public String toString() {
        return String.format("Новое событие. category - %d, title - %s, eventDate -", category, title) + eventDate.toString();
    }
}
