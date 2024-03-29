package ru.practicum.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.users.model.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    Collection<User> findAllByIdIn(List<Integer> ids);
}
