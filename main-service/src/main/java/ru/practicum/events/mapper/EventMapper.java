package ru.practicum.events.mapper;

import ru.practicum.categories.model.Category;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;

import static ru.practicum.categories.mapper.CategoryMapper.modelToCategoryDto;
import static ru.practicum.users.mapper.UserMapper.modelToUserShortDto;

public class EventMapper {

    public static Event newEventDtoToModel(NewEventDto newEventDto, Category eventCategory, User eventCreator) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(eventCategory)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(eventCreator)
                .location(newEventDto.getLocation())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventShortDto modelToEventShortDto(Event event, int confirmedRequests, int views) {

        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(modelToCategoryDto(event.getCategory()))
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(modelToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(views)
                .build();
    }

    public static EventFullDto modelToEventFullDto(Event event, int confirmedRequests, int views) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(modelToUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(views)
                .build();

    }


}
