package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User add(User user);

    User getById(Integer id);

    List<User> getAll();

    User update(User user, Integer id);

    User delete(Integer id);
}
