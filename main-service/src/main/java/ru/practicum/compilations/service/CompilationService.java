package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {

    // Admin: Подборки событий
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(int compilationId);

    CompilationDto updateCompilation(int compilationId, UpdateCompilationRequest updateCompilationRequest);

    // Public: Подборки событий
    Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(int compilationId);
}
