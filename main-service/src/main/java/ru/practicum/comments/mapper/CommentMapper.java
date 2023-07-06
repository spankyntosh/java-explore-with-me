package ru.practicum.comments.mapper;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.events.model.Event;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class CommentMapper {

    public static Comment newCommentDtoToModel(NewCommentDto newCommentDto, User commentator, Event event) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .commentator(commentator)
                .event(event)
                .createdOn(LocalDateTime.now())
                .likes(0)
                .build();
    }

    public static CommentDto modelToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .event(comment.getEvent().getId())
                .commentator(comment.getCommentator().getId())
                .createdOn(comment.getCreatedOn())
                .likes(comment.getLikes())
                .build();
    }

    public static Collection<CommentDto> modelToCommentDtos(Collection<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::modelToCommentDto)
                .collect(toList());
    }
}
