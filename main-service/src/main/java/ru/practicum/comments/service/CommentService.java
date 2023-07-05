package ru.practicum.comments.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;

import java.util.Collection;

public interface CommentService {

    CommentDto privateCreateComment(NewCommentDto newCommentDto, int userId, int eventId);

    CommentDto privateUpdateComment(UpdateCommentDto updateCommentDto, int commentId, int userId);

    void privateDeleteComment(int commentId, int userId);

    CommentDto adminUpdateComment(UpdateCommentDto updateCommentDto, int commentId);

    void adminDeleteComment(int commentId);

    Collection<CommentDto> publicGetComments(int eventId, PageRequest request);
}
