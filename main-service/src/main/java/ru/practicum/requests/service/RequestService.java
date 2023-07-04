package ru.practicum.requests.service;

import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.Collection;

public interface RequestService {

    ParticipationRequestDto createRequest(int userId, int eventId);

    Collection<ParticipationRequestDto> getRequests(int userId);

    ParticipationRequestDto cancelRequest(int userId, int requestId);
}
