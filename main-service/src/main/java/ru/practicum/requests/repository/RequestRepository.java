package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.enums.RequestStatus;
import ru.practicum.requests.model.EventConfirmedRequests;
import ru.practicum.requests.model.Request;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findAllByRequesterId(Integer requesterId);

    Optional<Request> findByEventIdAndRequesterId(Integer eventId, Integer userId);

    List<Request> findAllByEventIdAndStatus(Integer eventId, RequestStatus status);

    List<Request> findAllByEventId(Integer eventId);

    List<Request> findAllByIdIn(List<Integer> requestIds);

    @Query("SELECT request FROM Request request " +
            "WHERE request.event.id = :eventId " +
            "AND request.event.initiator.id = :userId")
    Collection<Request> findByEventIdAndInitiatorId(@Param("eventId") Integer eventId, @Param("userId") Integer userId);

    @Query("SELECT new ru.practicum.requests.model.EventConfirmedRequests(r.event.id, count(r.id)) " +
            "FROM Request AS r " +
            "WHERE r.event.id IN ?1 " +
            "AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id")
    List<EventConfirmedRequests> getEventsConfirmedRequests(List<Integer> eventIds);
}
