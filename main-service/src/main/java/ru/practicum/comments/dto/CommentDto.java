package ru.practicum.comments.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class CommentDto {

    private Integer id;
    private String text;
    private Integer event;
    private Integer commentator;
    private LocalDateTime createdOn;
    private Integer likes;
}
