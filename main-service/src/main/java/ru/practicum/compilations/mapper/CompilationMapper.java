package ru.practicum.compilations.mapper;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.Event;

import java.util.ArrayList;
import java.util.Collection;

public class CompilationMapper {

    public static Compilation newCompilationDtoToModel(NewCompilationDto newCompilationDto, Collection<Event> events) {

        if (newCompilationDto.getEvents() == null) {
            newCompilationDto.setEvents(new ArrayList<>());
        }
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .events(events)
                .build();
    }

    public static CompilationDto modelToCompilationDto(Compilation compilation, Collection<EventShortDto> eventShortDtos)  {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(eventShortDtos)
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .build();
    }
}
