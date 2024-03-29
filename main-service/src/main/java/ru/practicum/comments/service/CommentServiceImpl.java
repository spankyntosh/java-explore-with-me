package ru.practicum.comments.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.Like;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.comments.repository.LikeRepository;
import ru.practicum.events.enums.RequestStatus;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.comments.mapper.CommentMapper.*;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LikeRepository likeRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository,
                              RequestRepository requestRepository,
                              LikeRepository likeRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    public CommentDto privateCreateComment(NewCommentDto newCommentDto, int userId, int eventId) {
        User commentator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким id не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие с таким id не найдено"));
        if (event.getIsConfirmedParticipantsCreateComment()) {
            List<Request> confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            List<Integer> requesterIds = confirmedRequests.stream()
                    .map(request -> request.getRequester().getId())
                    .collect(Collectors.toList());
            if (!requesterIds.contains(userId)) {
                throw new ForbiddenException("Создавать комментарии могут участники с подтверждённым статусом заявки на событие");
            }
        }
        Comment comment = newCommentDtoToModel(newCommentDto, commentator, event);
        return modelToCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto privateUpdateComment(UpdateCommentDto updateCommentDto, int commentId, int userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с таким id не найден");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с таким id не найден"));
        comment.setText(updateCommentDto.getText());
        if (comment.getCommentator().getId() != userId) {
            throw new ForbiddenException(String.format("Пользователь с id %d не создавал комментарий с id %d", userId, commentId));
        }
        return modelToCommentDto(commentRepository.save(comment));
    }

    @Override
    public void privateDeleteComment(int commentId, int userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с таким id не найден"));
        if (comment.getCommentator().getId() != userId) {
            throw new ForbiddenException(String.format("Пользователь с id %d не создавал комментарий с id %d", userId, commentId));
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void privateAddLikeToComment(int commentId, int userId) {
        User commentator = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким id не найден"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с таким id не найден"));
        Like like = Like.builder()
                .user(commentator)
                .comment(comment)
                .build();
        likeRepository.save(like);
    }

    @Override
    public CommentDto adminUpdateComment(UpdateCommentDto updateCommentDto, int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с таким id не найден"));
        comment.setText(updateCommentDto.getText());
        return modelToCommentDto(commentRepository.save(comment));
    }

    @Override
    public void adminDeleteComment(int commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Комментарий с таким id не найден");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public Collection<CommentDto> publicGetComments(int eventId, PageRequest request) {
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException("Событие с таким id не найдено");
        }
        Collection<Comment> comments = commentRepository.findAllByEventId(eventId, request);
        Collection<Integer> commentIds = comments.stream()
                .map(comment -> comment.getId())
                .collect(Collectors.toList());

        Map<Integer, Integer> commentIdLikesMap = new HashMap<>();
        commentIds.forEach(commentId -> commentIdLikesMap.put(commentId, likeRepository.findByCommentId(commentId).size()));
        comments.stream()
                .forEach(comment -> {
                    comment.setLikes(commentIdLikesMap.get(comment.getId()));
                });

        return modelToCommentDtos(comments);
    }
}
