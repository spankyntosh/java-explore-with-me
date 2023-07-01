package ru.practicum.compilations.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.compilations.model.Compilation;

import java.util.Collection;


public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    Collection<Compilation> findAllByPinned(Boolean pinned, PageRequest request);
}
