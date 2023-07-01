package ru.practicum.requests.mapper;

import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.Request;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {

        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }

    public static Collection<ParticipationRequestDto> toParticipationRequestDtos(Collection<Request> requests) {
        return requests.stream()
                .map(request -> toParticipationRequestDto(request))
                .collect(toList());
    }
}
