package ru.practicum.events.service;

import ru.practicum.events.dto.*;
import ru.practicum.events.enums.EventState;
import ru.practicum.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

public interface EventService {

    // Private: События
    Collection<EventShortDto> privateGetEvents(int userId, Integer from, Integer size);

    EventFullDto privateCreateEvent(int userId, NewEventDto newEventDto);

    EventFullDto privateGetEvent(int userId, int eventId);

    EventFullDto patchEvent(int userId, int eventId, UpdateEventUserRequest updateEventRequest);

    Collection<ParticipationRequestDto> getUserEventRequests(int userId, int eventId);

    EventRequestStatusUpdateResult patchRequestsForUserEvent(int userId, int eventId, EventRequestStatusUpdateRequest statusUpdateRequest);

    // Public: События
    Collection<EventShortDto> publicGetEvents(String text, List<Integer> categories, Boolean paid, String rangeStart,
                                              String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                              HttpServletRequest servletRequest);

    EventFullDto publicGetEvent(int eventId, HttpServletRequest servletRequest);

    // Admin: События
    Collection<EventFullDto> adminGetEvents(List<Integer> users, List<EventState> states, List<Integer> categories,
                                            String rangeStart, String rangeEnd, Integer from, Integer size);


    EventFullDto adminPatchEvent(int eventId, UpdateEventAdminRequest adminUpdateRequest);
}
