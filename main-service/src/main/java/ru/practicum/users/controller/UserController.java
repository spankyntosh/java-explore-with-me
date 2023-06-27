package ru.practicum.users.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Validated
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User newUser) {
        log.info("Пришёл запрос на создание пользователя {}", newUser);
        return userService.createUser(newUser);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getUsers(@RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = "10", required = false) @Positive Integer size,
                                     @RequestParam(required = false) List<Integer> ids) {
        log.info("Пришёл запрос на получение пользователей. from - {}, size - {}, ids - {}", from, size, ids);
        return  userService.getUsers(from, size, ids);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer userId) {
        log.info("Пришёл запрос на удаление пользователя с id - {}", userId);
        userService.deleteUser(userId);
    }
}
