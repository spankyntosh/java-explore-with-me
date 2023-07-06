package ru.practicum.comments.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class NewCommentDto {

    @NotNull
    @NotBlank
    @Size(min = 2, max = 1000, message = "Текст комментария должен быть более 2 символов и менее 1000")
    private String text;
}
