package ru.practicum.events.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.enums.EventState;
import ru.practicum.events.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    Collection<Event> findAllByInitiatorId(Integer userId, PageRequest request);

    Optional<Event> findByInitiatorIdAndId(Integer initiatorId, Integer id);

    @Query("select event from Event event " +
            "where event.id IN (:ids)")
    Collection<Event> findByIds(@Param("ids") Collection<Integer> ids);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE " +
            "(" +
            ":text IS NULL " +
            "OR LOWER(e.description) LIKE CONCAT('%', :text, '%') " +
            "OR LOWER(e.annotation) LIKE CONCAT('%', :text, '%')" +
            ")" +
            "AND (:states IS NULL OR e.state IN (:states)) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR e.eventDate <= :rangeEnd) " +
            "order by e.eventDate")
    List<Event> findByParamsOrderByDate(
            @Param("text") String text,
            @Param("states") List<EventState> states,
            @Param("categories") List<Integer> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            PageRequest request);


    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE " +
            "((:states IS NULL OR e.state IN (:states)) " +
            "AND (:users IS NULL OR e.initiator.id IN (:users)) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR e.eventDate <= :rangeEnd) " +
            "order by e.eventDate")
    List<Event> findByParamsByAdmin(@Param("users") List<Integer> users,
                                    @Param("states") List<EventState> states,
                                    @Param("categories")List<Integer> categories,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    PageRequest request);

    List<Event> findByCategory(Integer categoryId);
}
