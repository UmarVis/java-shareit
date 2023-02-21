package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserRepositoryIml implements UserRepository {
    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer userId = 0;

    @Override
    public User add(User user) {
        checkEmail(user, user.getId());
        userId++;
        user.setId(userId);
        userMap.put(userId, user);
        log.info("User with id {} was created", userId);
        return userMap.get(userId);
    }

    @Override
    public User getById(Integer id) {
        if (userMap.get(id) == null) {
            log.error("Id not found {} ", id);
            throw new UserNotFoundException("User with id: " + id + " not found");
        }
        log.info("User with ID {} was received", id);
        return userMap.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User delete(Integer id) {
        User deleteUser = userMap.get(id);
        userMap.remove(id);
        log.info("User with id {} delete", id);
        return deleteUser;
    }

    @Override
    public void checkEmail(User user, Integer id) {
        List<User> userWithSameEmail = getAll()
                .stream()
                .filter(u -> Objects.equals(u.getEmail(), user.getEmail()) && !Objects.equals(u.getId(), id))
                .collect(Collectors.toList());

        if (!userWithSameEmail.isEmpty()) {
            throw new ValidationException("Пользователь с такой почтой уже есть");
        }
    }
}
