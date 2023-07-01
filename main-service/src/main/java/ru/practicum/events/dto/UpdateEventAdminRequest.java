package ru.practicum.events.dto;

import lombok.*;
import ru.practicum.events.enums.EventStateAction;
import ru.practicum.events.model.Location;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class UpdateEventAdminRequest {

    @NotBlank(message = "Описание не должно быть пустым")
    @Size(min = 20, max = 2000, message = "текст описания должен быть больше 20 и меньше 2000 символов")
    private String annotation;
    private Integer category;
    @NotBlank(message = "Описание события не должно быть пустым")
    @Size(min = 20, max = 7000, message = "текст описания события должен быть больше 20 и меньше 7000 символов")
    private String description;
    @FutureOrPresent(message = "дата события не должна быть в прошлом")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    EventStateAction stateAction;
    @NotBlank
    @Size(min = 3, max = 120, message = "текст заголовка должен быть больше 3 и меньше 120 символов")
    private String title;
}
