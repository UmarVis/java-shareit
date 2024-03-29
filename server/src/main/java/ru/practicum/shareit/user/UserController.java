package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @GetMapping("{id}")
    public UserDto getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PatchMapping("{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable Integer id) {
        return userService.update(userDto, id);
    }

    @DeleteMapping("{id}")
    public UserDto delete(@PathVariable Integer id) {
        return userService.delete(id);
    }
}
