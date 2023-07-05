package ru.practicum.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Like;

import java.util.Collection;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    Collection<Like> findByCommentId(Integer commentId);
}
