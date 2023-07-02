package ru.practicum.compilations.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.common.StatsResponseDTO;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.requests.model.EventConfirmedRequests;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.statistics.StatisticsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static ru.practicum.compilations.mapper.CompilationMapper.modelToCompilationDto;
import static ru.practicum.compilations.mapper.CompilationMapper.newCompilationDtoToModel;
import static ru.practicum.events.mapper.EventMapper.modelToEventShortDto;

@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final StatisticsService statisticsService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository,
                                  RequestRepository requestRepository,
                                  EventRepository eventRepository,
                                  StatisticsService statisticsService) {
        this.compilationRepository = compilationRepository;
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.statisticsService = statisticsService;
    }

    // Admin: Подборки событий
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Collection<Event> events = eventRepository.findByIds(newCompilationDto.getEvents());
        List<EventShortDto> eventShortDtos = getEventShortDtos(List.copyOf(events));
        return modelToCompilationDto(compilationRepository.save(newCompilationDtoToModel(newCompilationDto, events)), eventShortDtos);
    }

    @Override
    public void deleteCompilation(int compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new EntityNotFoundException("Подборка с таким id не найдена");
        }
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public CompilationDto updateCompilation(int compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка с таким id не найдена"));
        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(eventRepository.findByIds(updateCompilationRequest.getEvents()));
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        compilationRepository.save(compilation);
        List<EventShortDto> eventShortDtos = getEventShortDtos(List.copyOf(compilation.getEvents()));
        return modelToCompilationDto(compilation, eventShortDtos);
    }

    // Public: Подборки событий
    @Override
    public Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest request = PageRequest.of(from / size, size);
        Collection<Compilation> returnedCompilations;
        if (nonNull(pinned)) {
            returnedCompilations = compilationRepository.findAllByPinned(pinned, request);
        } else {
            returnedCompilations = compilationRepository.findAll(request).toList();
        }

        Set<Event> uniqueEvents = new HashSet<>();
        returnedCompilations.forEach(compilation -> uniqueEvents.addAll(compilation.getEvents()));

        List<EventShortDto> uniqueEventShortDtos = getEventShortDtos(List.copyOf(uniqueEvents));

        return returnedCompilations.stream()
                .map(compilation -> {
                    List<EventShortDto> compilationEventShortDtos = uniqueEventShortDtos.stream()
                            .filter(eventShortDto -> compilation.getEvents().stream().map(Event::getId)
                                    .collect(toList()).contains(eventShortDto.getId())).collect(Collectors.toList());
                    return modelToCompilationDto(compilation, compilationEventShortDtos);
                })
                .collect(toList());
    }

    @Override
    public CompilationDto getCompilation(int compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка с таким id не найдена"));
        List<EventShortDto> eventShortDtos = getEventShortDtos(List.copyOf(compilation.getEvents()));
        return modelToCompilationDto(compilation, eventShortDtos);
    }

    // Приватные методы класса
    private Map<Integer, Integer> getViews(List<Event> events) {
        // Создаем отображение
        Map<Integer, Integer> eventIdsViewsMap = new HashMap<>();
        // Предварительно заполняем нулями количество просмотров для каждого события
        events.forEach(event -> eventIdsViewsMap.put(event.getId(), 0));
        // Формируем список uri для отправки в сервис статистики
        List<String> uris = events.stream()
                .map(Event::getId)
                .map(eventId -> String.format("/events/%d", eventId))
                .collect(toList());
        // Ищем самую раннюю дату опубликованного события
        Optional<LocalDateTime> earliestDateTime = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);
        // Если самая ранняя дата присутствует, то отправляем запрос в сервис статистики
        if (earliestDateTime.isPresent()) {
            List<StatsResponseDTO> responseFromStatsService = statisticsService
                    .getStatistics(earliestDateTime.get().format(formatter), LocalDateTime.now().format(formatter), uris, false);
            responseFromStatsService.stream().forEach(eventStats -> {
                Integer eventId = Integer.parseInt(eventStats.getUri().split("/")[2]);
                eventIdsViewsMap.put(eventId, (int) eventStats.getHits());
            });
        }
        // Возвращаем отображение
        return eventIdsViewsMap;
    }

    private List<EventShortDto> getEventShortDtos(List<Event> events) {
        Map<Integer, Integer> eventIdsViewsMap = getViews(events);
        Map<Integer, Integer> eventIdConfirmedRequestsMap = new HashMap<>();
        // Предварительно заполняем нулями количество подтверждённых запросов для каждого события
        events.forEach(event -> eventIdConfirmedRequestsMap.put(event.getId(), 0));
        List<EventConfirmedRequests> eventConfirmedRequests = requestRepository
                .getEventsConfirmedRequests(events.stream().map(Event::getId).collect(toList()));
        for (EventConfirmedRequests confRequest : eventConfirmedRequests) {
            eventIdConfirmedRequestsMap.put(confRequest.getEventId(), confRequest.getConfirmedRequests());
        }
        return events.stream()
                .map(event -> modelToEventShortDto(event, eventIdConfirmedRequestsMap.get(event.getId()), eventIdsViewsMap.get(event.getId())))
                .collect(toList());
    }
}
