package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validate.Create;
import ru.practicum.shareit.validate.Update;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Validated(Create.class) UserDto userDto) {
        log.info("Создан новый пользователь {}", userDto.getName());
        return userClient.add(userDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(@PathVariable Integer id) {
        log.info("Получен пользователь {}", id);
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Получены все пользователи");
        return userClient.getAll();
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated(Update.class) UserDto userDto,
                                             @PathVariable Integer id) {
        log.info("Пользователь обновлен {}", id);
        return userClient.updateUser(userDto, id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        log.info("Пользователь с ИД {} удален", id);
        return userClient.delete(id);
    }
}
