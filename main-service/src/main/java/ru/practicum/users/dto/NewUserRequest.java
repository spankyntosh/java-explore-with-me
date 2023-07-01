package ru.practicum.users.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class NewUserRequest {

    @NotBlank
    private String name;
    @NotBlank
    @Email(message = "неверный формат электронной почты")
    private String email;

    @Override
    public String toString() {
        return String.format("Новый пользователь: имя - %s, почта - %s", this.name, this.email);
    }
}
