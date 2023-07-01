package ru.practicum.requests.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping("users/{userId}/requests")
@Validated
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Collection<ParticipationRequestDto> getRequests(@PathVariable @Positive int userId) {
        log.info("Пришёл запросов на получение запросов об участии в событиях от пользователя с id {}", userId);
        return requestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto createRequest(@PathVariable @Positive int userId,
                                          @RequestParam @Positive int eventId) {
        log.info("Пришёл запрос с заявкой на участие в событии с id {} от пользователя с id {}", eventId, userId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    ParticipationRequestDto cancelRequest(@PathVariable @Positive int userId,
                                          @PathVariable @Positive int requestId) {
        log.info("Пришёл запрос об отмене участия в событии от пользователя с id {}", userId);
        return requestService.cancelRequest(userId, requestId);
    }
}
