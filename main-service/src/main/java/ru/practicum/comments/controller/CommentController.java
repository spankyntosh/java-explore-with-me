package ru.practicum.comments.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@Valid
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/users/{userId}/comments/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto privateCreateComment(@Valid @RequestBody NewCommentDto newCommentDto,
                                           @PathVariable @Positive int userId,
                                           @PathVariable @Positive int eventId) {
        log.info("Пришёл запрос на создание комментария");
        return commentService.privateCreateComment(newCommentDto, userId, eventId);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto privateUpdateComment(@Valid @RequestBody UpdateCommentDto updateCommentDto,
                                           @PathVariable @Positive int commentId,
                                           @PathVariable @Positive int userId) {
        log.info("Пришёл запрос на обновление комментария");
        return commentService.privateUpdateComment(updateCommentDto, commentId, userId);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void privateDeleteComment(@PathVariable @Positive int commentId,
                                     @PathVariable @Positive int userId) {
        log.info("Пришёл запрос на удаление комментария");
        commentService.privateDeleteComment(commentId, userId);
    }

    @PutMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void privateAddLikeToComment(@PathVariable @Positive int userId,
                                        @PathVariable @Positive int commentId) {
        log.info("Пришёл запрос на добавление лайка комментарию");
        commentService.privateAddLikeToComment(commentId, userId);
    }

    @PatchMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto adminUpdateComment(@Valid @RequestBody UpdateCommentDto updateCommentDto,
                                         @PathVariable @Positive int commentId) {
        log.info("Пришёл запрос на обновление комментария администратором");
        return commentService.adminUpdateComment(updateCommentDto, commentId);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteComment(@PathVariable @Positive int commentId) {
        log.info("Пришёл запрос на удаление комментария администратором");
        commentService.adminDeleteComment(commentId);
    }

    @GetMapping("/comments/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentDto> publicGetComments(@PathVariable @Positive int eventId,
                                                    @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(required = false, defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("Пришёл запрос на получение комментариев по событию с id {}", eventId);
        PageRequest request = PageRequest.of(from / size, size);
        return commentService.publicGetComments(eventId, request);
    }

}
