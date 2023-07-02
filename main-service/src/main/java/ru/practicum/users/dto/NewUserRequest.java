package ru.practicum.users.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class NewUserRequest {

    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
    @NotBlank
    @Email(message = "неверный формат электронной почты")
    @Size(min = 6, max = 254)
    private String email;

    @Override
    public String toString() {
        return String.format("Новый пользователь: имя - %s, почта - %s", this.name, this.email);
    }
}
