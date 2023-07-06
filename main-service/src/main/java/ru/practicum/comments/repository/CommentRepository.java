package ru.practicum.comments.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Collection<Comment> findAllByEventId(int eventId, PageRequest request);
}
