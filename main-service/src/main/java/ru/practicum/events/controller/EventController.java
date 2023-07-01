package ru.practicum.events.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@RestController
@Validated
@Slf4j
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Private: События
    @GetMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDto> getEvents(@PathVariable @Positive int userId,
                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришёл запрос от пользователя с id {} на получение событий", userId);
        return eventService.privateGetEvents(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable @Positive int userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Пришёл запрос от пользователя с id {} на добавление события {}", userId, newEventDto);
        return eventService.privateCreateEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable @Positive int userId,
                                 @PathVariable @Positive int eventId) {
        log.info("Пришёл запрос от пользователя с id {} на получение события с id {}", userId, eventId);
        return eventService.privateGetEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchEvent(@PathVariable @Positive int userId,
                                   @PathVariable @Positive int eventId,
                                   @Valid @RequestBody UpdateEventUserRequest updateEventRequest) {
        log.info("Пришёл запрос от пользователя с id {} на обновление события с id {}", userId, eventId);
        return eventService.patchEvent(userId, eventId, updateEventRequest);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getUserEventRequests(@PathVariable @Positive int userId,
                                                                    @PathVariable @Positive int eventId) {
        log.info("Пришёл запрос от пользователя с id {} на получение инфо о запросах на участие в событии с id {}", userId, eventId);
        return eventService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult patchRequestsForUserEvent(@PathVariable @Positive int userId,
                                                                    @PathVariable @Positive int eventId,
                                                                    @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Пришёл запрос от пользователя с id {} на изменение статуса заявок у события с id {}", userId, eventId);
        return eventService.patchRequestsForUserEvent(userId, eventId, eventRequestStatusUpdateRequest);
    }

    // Public: События
    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    Collection<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                        @RequestParam(required = false) List<Integer> categories,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(required = false) Boolean onlyAvailable,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(required = false, defaultValue = "10") @Positive int size,
                                        HttpServletRequest servletRequest) {
            log.info("Пришёл публичный запрос на получение событий");
            return eventService.publicGetEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, servletRequest);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto getEvent(@PathVariable @Positive int id, HttpServletRequest servletRequest) {
        log.info("Пришёл запрос на получение события с id {}", id);
        return eventService.publicGetEvent(id, servletRequest);
    }

    // Admin: События
    @GetMapping("/admin/events")
    @ResponseStatus(HttpStatus.OK)
    Collection<EventFullDto> adminGetEvents(@RequestParam(required = false) List<Integer> users,
                                            @RequestParam(required = false) List<EventState> states,
                                            @RequestParam(required = false) List<Integer> categories,
                                            @RequestParam(required = false) String rangeStart,
                                            @RequestParam(required = false) String rangeEnd,
                                            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Пришёл запрос от администратора на получение событий");
        return eventService.adminGetEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    EventFullDto adminPatchEvent(@PathVariable @Positive int eventId,
                                 @Valid @RequestBody UpdateEventAdminRequest adminRequest) {
        log.info("Пришёл запрос от администратора на обновление события с id {}", eventId);
        return eventService.adminPatchEvent(eventId, adminRequest);
    }
}
