package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto getById(Integer id);

    UserDto update(UserDto userDto, Integer id);

    UserDto delete(Integer id);

    List<UserDto> getAll();

}
