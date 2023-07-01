package ru.practicum.events.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoriesRepository;
import ru.practicum.common.StatsResponseDTO;
import ru.practicum.events.dto.*;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.enums.RequestStatus;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.Location;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.repository.LocationRepository;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.EventConfirmedRequests;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.statistics.HtiMapper;
import ru.practicum.statistics.StatisticsService;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static ru.practicum.events.mapper.EventMapper.*;
import static ru.practicum.requests.mapper.RequestMapper.toParticipationRequestDtos;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatisticsService statisticsService;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            CategoriesRepository categoriesRepository,
                            UserRepository userRepository,
                            LocationRepository locationRepository,
                            RequestRepository requestRepository,
                            StatisticsService statisticsService) {
        this.eventRepository = eventRepository;
        this.categoriesRepository = categoriesRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.requestRepository = requestRepository;
        this.statisticsService = statisticsService;
    }

    // Private: События
    @Override
    public Collection<EventShortDto> privateGetEvents(int userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с таким id не существует");
        }
        PageRequest request = PageRequest.of(from / size, size);
        Collection<Event> userEvents = eventRepository.findAllByInitiatorId(userId, request);
        List<Event> publishedEvents = userEvents.stream()
                .filter(event -> event.getState().equals(EventState.PUBLISHED))
                .collect(toList());
        List<Event> notPublishedEvents = userEvents.stream()
                .filter(event -> !event.getState().equals(EventState.PUBLISHED))
                .collect(toList());
        List<EventShortDto> notPublishedEventShortDtos = notPublishedEvents.stream()
                .map(event -> modelToEventShortDto(event, 0, 0))
                .collect(toList());
        List<EventShortDto> publishedEventShortDtos = getPublishedEventShortDtos(publishedEvents);
        publishedEventShortDtos.addAll(notPublishedEventShortDtos);
        return publishedEventShortDtos;
    }

    @Override
    public EventFullDto privateCreateEvent(int userId, NewEventDto newEventDto) {
        if (!newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ForbiddenException("Событие не может начинаться менее чем за 2 часа от текущего времени");
        }
        User eventCreator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким id не существует"));
        Category eventCategory = categoriesRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Категория с таким id не найдена"));
        Location eventLocation = locationRepository.findByLatAndLon(newEventDto.getLocation().getLon(), newEventDto.getLocation().getLat())
                .orElseGet(() -> locationRepository.save(newEventDto.getLocation()));
        Event newEvent = newEventDtoToModel(newEventDto, eventCategory, eventCreator);
        return modelToEventFullDto(eventRepository.save(newEvent), 0, 0);
    }

    @Override
    public EventFullDto privateGetEvent(int userId, int eventId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с таким id не существует");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Событие с таким id не существует");
        }
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не создавал событие с id %d", userId, eventId)));
        int confirmedRequests = 0;
        int views = 0;
        if (event.getState().equals(EventState.PUBLISHED)) {
            confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size();
            views = getViews(event);
        }

        return modelToEventFullDto(event, confirmedRequests, views);
    }

    @Override
    public EventFullDto patchEvent(int userId, int eventId, UpdateEventUserRequest updateEventRequest) {

        if (nonNull(updateEventRequest.getEventDate())) {
            if (!updateEventRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
                throw new ForbiddenException("Событие не может начинаться менее чем за 2 часа от текущего времени");
            }
        }
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с таким id не существует");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Событие с таким id не существует");
        }
        Event updatedEvent = eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не создавал событие с id %d", userId, eventId)));
        if (updatedEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Изменять можно только неопубликованные или отмененные события.");
        }
        if (updateEventRequest.getAnnotation() != null) {
            updatedEvent.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            updatedEvent.setCategory(categoriesRepository.findById(updateEventRequest.getCategory()).get());
        }
        if (updateEventRequest.getDescription() != null) {
            updatedEvent.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            updatedEvent.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getLocation() != null) {
            updatedEvent.setLocation(locationRepository.findByLatAndLon(updateEventRequest.getLocation().getLon(), updateEventRequest.getLocation().getLat())
                    .orElseGet(() -> locationRepository.save(updateEventRequest.getLocation())));
        }
        if (updateEventRequest.getPaid() != null) {
            updatedEvent.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            updatedEvent.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            updatedEvent.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (updateEventRequest.getStateAction() != null) {
            switch (updateEventRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    updatedEvent.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    updatedEvent.setState(EventState.CANCELED);
                    break;
            }
        }
        if (updateEventRequest.getTitle() != null) {
            updatedEvent.setTitle(updateEventRequest.getTitle());
        }

        int confirmedRequests = 0;
        int views = 0;
        if (nonNull(updatedEvent.getPublishedOn())) {
            confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size();
            views = getViews(updatedEvent);
        }
        return modelToEventFullDto(eventRepository.save(updatedEvent), confirmedRequests, views);
    }

    @Override
    public Collection<ParticipationRequestDto> getUserEventRequests(int userId, int eventId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с таким id не существует");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Событие с таким id не существует");
        }
        eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не создавал событие с id %d", userId, eventId)));

        return toParticipationRequestDtos(requestRepository.findByEventIdAndInitiatorId(eventId, userId));
    }

    @Override
    public EventRequestStatusUpdateResult patchRequestsForUserEvent(int userId, int eventId, EventRequestStatusUpdateRequest statusUpdateRequest) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с таким id не существует");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Событие с таким id не существует");
        }
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id %d не создавал событие с id %d", userId, eventId)));

        if (!event.getRequestModeration() ||
                event.getParticipantLimit() == 0 ||
                statusUpdateRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }
        int numberOfAlreadyConfirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size();
        if (numberOfAlreadyConfirmedRequests == event.getParticipantLimit()) {
            throw new ForbiddenException("Достигнут лимит подтверждённых запросов");
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        List<Request> requestsForStatusChange = requestRepository.findAllByIdIn(statusUpdateRequest.getRequestIds());

        if (requestsForStatusChange.size() != statusUpdateRequest.getRequestIds().size()) {
            throw new EntityNotFoundException("Не найдены некоторые заявки из запроса на изменение статуса");
        }

        boolean statusCondition = requestsForStatusChange.stream()
                .map(Request::getStatus)
                .allMatch(RequestStatus.PENDING::equals);
        if (!statusCondition) {
            throw new ForbiddenException("Изменить статус возможно только у заявок находящихся в ожидании");
        }

        switch (statusUpdateRequest.getStatus()) {
            case REJECTED: {
                requestsForStatusChange.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                rejectedRequests.addAll(requestRepository.saveAll(requestsForStatusChange));
                break;
            }
            case CONFIRMED: {
                if (numberOfAlreadyConfirmedRequests + requestsForStatusChange.size() < event.getParticipantLimit()) {
                    requestsForStatusChange.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                    confirmedRequests.addAll(requestRepository.saveAll(requestsForStatusChange));
                }
                if (numberOfAlreadyConfirmedRequests + requestsForStatusChange.size() == event.getParticipantLimit()) {
                    requestsForStatusChange.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                    confirmedRequests.addAll(requestRepository.saveAll(requestsForStatusChange));
                    List<Request> pendingRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
                    pendingRequests.forEach(request -> request.setStatus(RequestStatus.CANCELED));
                    rejectedRequests.addAll(requestRepository.saveAll(pendingRequests));
                }
                if (numberOfAlreadyConfirmedRequests + requestsForStatusChange.size() > event.getParticipantLimit()) {
                    requestsForStatusChange.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                    rejectedRequests.addAll(requestRepository.saveAll(requestsForStatusChange));
                }
            }
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(List.copyOf(toParticipationRequestDtos(confirmedRequests)))
                .rejectedRequests(List.copyOf(toParticipationRequestDtos(rejectedRequests)))
                .build();

    }

    // Public: События
    @Override
    public Collection<EventShortDto> publicGetEvents(String text, List<Integer> categories, Boolean paid,
                                                     String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort,
                                                     Integer from, Integer size, HttpServletRequest servletRequest) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (nonNull(rangeStart)) {
            start = LocalDateTime.parse(rangeStart, formatter);
        }
        if (nonNull(rangeEnd)) {
            end = LocalDateTime.parse(rangeEnd, formatter);
        }
        if (text == null) {
            text = "";
        }
        PageRequest request = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.findByParamsOrderByDate(text.toLowerCase(),
                List.of(EventState.PUBLISHED),
                categories,
                paid,
                start,
                end,
                request);

        List<EventShortDto> eventShortDtos = getPublishedEventShortDtos(events);

        Map<Integer, Integer> eventsParticipantLimit = new HashMap<>();
        events.forEach(event -> eventsParticipantLimit.put(event.getId(), event.getParticipantLimit()));

        if (onlyAvailable) {
            eventShortDtos = eventShortDtos.stream()
                    .filter(eventShortDto -> eventShortDto.getConfirmedRequests() < eventsParticipantLimit.get(eventShortDto.getId()))
                    .collect(toList());
        }

        if (nonNull(sort) && sort.equalsIgnoreCase("VIEWS")) {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews));
        }
        statisticsService.addHit(HtiMapper.toHitRequestDTO("main-service", servletRequest));
        return eventShortDtos;
    }

    @Override
    public EventFullDto publicGetEvent(int eventId, HttpServletRequest servletRequest) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с таким id не существует"));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EntityNotFoundException("Событие с таким id не опубликовано.");
        }
        statisticsService.addHit(HtiMapper.toHitRequestDTO("main-service", servletRequest));
        int confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size();
        int views = getViews(event);

        return modelToEventFullDto(event, confirmedRequests, views);
    }

    // Admin: События
    @Override
    public Collection<EventFullDto> adminGetEvents(List<Integer> users, List<EventState> states, List<Integer> categories,
                                                   String rangeStart, String rangeEnd, Integer from, Integer size) {
        PageRequest request = PageRequest.of(from / size, size);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (nonNull(rangeStart)) {
            start = LocalDateTime.parse(rangeStart, formatter);
        }
        if (nonNull(rangeEnd)) {
            end = LocalDateTime.parse(rangeEnd, formatter);
        }
        List<Event> events = eventRepository.findByParamsByAdmin(users, states, categories, start, end, request);
        return getEventFullDtos(events);
    }

    @Override
    public EventFullDto adminPatchEvent(int eventId, UpdateEventAdminRequest adminUpdateRequest) {
        if (nonNull(adminUpdateRequest.getEventDate())) {
            if (!adminUpdateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1L))) {
                throw new ForbiddenException("Событие не может начинаться менее чем за 2 часа от текущего времени");
            }
        }
        Event updatedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с таким id не найдено"));

        if (adminUpdateRequest.getAnnotation() != null) {
            updatedEvent.setAnnotation(adminUpdateRequest.getAnnotation());
        }
        if (adminUpdateRequest.getDescription() != null) {
            updatedEvent.setDescription(adminUpdateRequest.getDescription());
        }
        if (adminUpdateRequest.getCategory() != null) {
            updatedEvent.setCategory(categoriesRepository.findById(adminUpdateRequest.getCategory()).get());
        }
        if (adminUpdateRequest.getEventDate() != null) {
            updatedEvent.setEventDate(adminUpdateRequest.getEventDate());
        }
        if (adminUpdateRequest.getPaid() != null) {
            updatedEvent.setPaid(adminUpdateRequest.getPaid());
        }
        if (adminUpdateRequest.getLocation() != null) {
            updatedEvent.setLocation(locationRepository.findByLatAndLon(adminUpdateRequest.getLocation().getLon(), adminUpdateRequest.getLocation().getLat())
                    .orElseGet(() -> locationRepository.save(adminUpdateRequest.getLocation())));
        }
        if (adminUpdateRequest.getParticipantLimit() != null) {
            updatedEvent.setParticipantLimit(adminUpdateRequest.getParticipantLimit());
        }
        if (adminUpdateRequest.getRequestModeration() != null) {
            updatedEvent.setRequestModeration(adminUpdateRequest.getRequestModeration());
        }
        if (adminUpdateRequest.getStateAction() != null) {
            if (!updatedEvent.getState().equals(EventState.PENDING)) {
                throw new ForbiddenException(String.format("Опубликовать можно только " +
                        "события, находящиеся в ожидании публикации. Текущий статус: %s", updatedEvent.getState()));
            }

            switch (adminUpdateRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    updatedEvent.setState(EventState.PUBLISHED);
                    updatedEvent.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    updatedEvent.setState(EventState.REJECTED);
                    break;
            }
        }
        if (adminUpdateRequest.getTitle() != null) {
            updatedEvent.setTitle(adminUpdateRequest.getTitle());
        }

        int confirmedRequests = 0;
        int views = 0;
        if (nonNull(updatedEvent.getPublishedOn())) {
            confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED).size();
            views = getViews(updatedEvent);
        }
        return modelToEventFullDto(eventRepository.save(updatedEvent), confirmedRequests, views);
    }

    // Приватные методы класса
    private int getViews(Event event) {
        List<String> uris = List.of("events/" + event.getId());
        List<StatsResponseDTO> responseFromStatsService = statisticsService
                .getStatistics(event.getPublishedOn(), LocalDateTime.now(), uris, false);
        return (int) responseFromStatsService.get(0).getHits();
    }

    private Map<Integer, Integer> getViews(List<Event> events) {
        Map<Integer, Integer> eventIdsViewsMap = new HashMap<>();
        List<String> uris = events.stream()
                .map(Event::getId)
                .map(eventId -> String.format("/events/%d", eventId))
                .collect(toList());
        LocalDateTime earliestDateTime = events.stream()
                .map(Event::getPublishedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .get();
        List<StatsResponseDTO> responseFromStatsService = statisticsService
                .getStatistics(earliestDateTime, LocalDateTime.now(), uris, false);
        responseFromStatsService.stream().forEach(eventStats -> {
            Integer eventId = Integer.parseInt(eventStats.getUri().split("/")[2]);
            eventIdsViewsMap.put(eventId, (int) eventStats.getHits());
        });
        return eventIdsViewsMap;
    }

    private List<EventShortDto> getPublishedEventShortDtos(List<Event> events) {
        Map<Integer, Integer> views = getViews(events);
        Map<Integer, Integer> confirmedRequests = new HashMap<>();
        List<EventConfirmedRequests> eventConfirmedRequests = requestRepository
                .getEventsConfirmedRequests(events.stream().map(Event::getId).collect(toList()));
        for (EventConfirmedRequests confRequest : eventConfirmedRequests) {
            confirmedRequests.put(confRequest.getEventId(), confRequest.getConfirmedRequests());
        }
        return events.stream()
                .map(event -> modelToEventShortDto(event, confirmedRequests.get(event.getId()), views.get(event.getId())))
                .collect(toList());
    }

    private List<EventFullDto> getEventFullDtos(List<Event> events) {
        Map<Integer, Integer> views = getViews(events);
        Map<Integer, Integer> confirmedRequests = new HashMap<>();
        List<EventConfirmedRequests> eventConfirmedRequests = requestRepository
                .getEventsConfirmedRequests(events.stream().map(Event::getId).collect(toList()));
        for (EventConfirmedRequests confRequest : eventConfirmedRequests) {
            confirmedRequests.put(confRequest.getEventId(), confRequest.getConfirmedRequests());
        }
        return events.stream()
                .map(event -> modelToEventFullDto(event, confirmedRequests.get(event.getId()), views.get(event.getId())))
                .collect(toList());
    }

}
