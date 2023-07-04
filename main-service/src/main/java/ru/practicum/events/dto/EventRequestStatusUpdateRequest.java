package ru.practicum.events.dto;

import lombok.*;
import ru.practicum.events.enums.RequestStatusAction;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class EventRequestStatusUpdateRequest {

    @NotEmpty
    private List<Integer> requestIds;
    @NotNull
    private RequestStatusAction status;
}
