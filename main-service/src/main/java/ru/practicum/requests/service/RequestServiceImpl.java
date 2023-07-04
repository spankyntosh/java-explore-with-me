package ru.practicum.requests.service;

import org.springframework.stereotype.Service;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.enums.RequestStatus;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.practicum.requests.mapper.RequestMapper.toParticipationRequestDto;
import static ru.practicum.requests.mapper.RequestMapper.toParticipationRequestDtos;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestServiceImpl(RequestRepository requestRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public ParticipationRequestDto createRequest(int userId, int eventId) {

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким id не существует"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с таким id не существует"));

        if (event.getInitiator().getId() == userId) {
            throw new ForbiddenException("Невозможно создать запрос на собственное событие.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Невозможно создать запрос на неопубликованное событие.");
        }

        Optional<Request> previousRequest = requestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (previousRequest.isPresent()) {
            throw new ForbiddenException("Невозможно создать повторный запрос.");
        }

        List<Request> confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int numberOfConfirmedRequests = confirmedRequests.size();
        if (numberOfConfirmedRequests != 0 && (numberOfConfirmedRequests + 1) > event.getParticipantLimit()) {
            throw new ForbiddenException("Достигнут лимит подтвержденных запросов на участие");
        }

        Request newRequest = Request.builder()
                .event(event)
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }

        return toParticipationRequestDto(requestRepository.save(newRequest));
    }

    @Override
    public Collection<ParticipationRequestDto> getRequests(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с таким id не существует");
        }
        return toParticipationRequestDtos(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public ParticipationRequestDto cancelRequest(int userId, int requestId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с таким id не существует");
        }
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Заявки на участие с таким id не существует"));

        if (request.getRequester().getId() != userId) {
            throw new ForbiddenException("Невозможно отменить чужой запрос");
        }
        request.setStatus(RequestStatus.CANCELED);
        return toParticipationRequestDto(requestRepository.save(request));
    }
}
