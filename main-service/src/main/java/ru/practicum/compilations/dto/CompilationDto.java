package ru.practicum.compilations.dto;

import lombok.*;
import ru.practicum.events.dto.EventShortDto;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CompilationDto {

    private Integer id;
    private Boolean pinned;
    private String title;
    private Collection<EventShortDto> events;
}
