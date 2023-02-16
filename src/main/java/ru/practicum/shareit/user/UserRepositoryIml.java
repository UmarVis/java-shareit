package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserRepositoryIml implements UserRepository {
    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer userId = 0;

    public User add(User user) {
        checkEmail(user);
        userId++;
        user.setId(userId);
        userMap.put(userId, user);
        log.info("User with id {} was created", userId);
        return userMap.get(userId);
    }

    public User getById(Integer id) {
        log.info("User with ID {} was received", id);
        return userMap.get(id);
    }

    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }

    public User update(User user, Integer id) {
        userMap.put(id, user);
        log.info("User with id {} updated", id);
        return userMap.get(id);
    }

    public User delete(Integer id) {
        User deleteUser = userMap.get(id);
        userMap.remove(id);
        log.info("User with id {} delete", id);
        return deleteUser;
    }

    private void checkEmail(User user) {
        List<User> userWithSameEmail = getAll()
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .filter(u -> !Objects.equals(u.getId(), user.getId()))
                .collect(Collectors.toList());

        if (!userWithSameEmail.isEmpty()) {
            throw new ValidationException("Пользователь с такой почтой уже есть");
        }
    }
}
