package ru.practicum.users.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    @NotBlank
    private String name;
    @Column(name = "email")
    @NotBlank
    @Email(message = "неверный формат электронной почты")
    private String email;

    @Override
    public String toString() {
        return String.format("Пользователь: имя - %s, почта - %s", this.name, this.email);
    }
}
