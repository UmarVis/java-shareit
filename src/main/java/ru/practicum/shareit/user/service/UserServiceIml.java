package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceIml implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto add(UserDto userDto) {
        User user;
        try {
            user = userRepository.save(mapper.makeUser(userDto));
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Пользователь с такой почтой уже есть");
        }
        log.info("Пользователь с именем {} создан", user.getName());
        return mapper.makeUserDto(user);
    }

    @Override
    public UserDto getById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Пользователь с ИД " + id + " не найден"));
        log.info("Получен пользователь с ИД {}", id);
        return mapper.makeUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Integer id) {
        UserDto updateUser = getById(id);
        if (userDto.getName() != null && !(userDto.getName().isBlank())) {
            updateUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !(userDto.getEmail().isBlank())) {
            updateUser.setEmail(userDto.getEmail());
        }
        log.info("Полтьователь с ИД {} обновлен", id);
        userRepository.save(mapper.makeUser(updateUser));
        return updateUser;
    }

    @Override
    public UserDto delete(Integer id) {
        UserDto userDto = getById(id);
        userRepository.deleteById(id);
        log.info("Пользователь с ИД {} удален", id);
        return userDto;
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(mapper::makeUserDto).collect(Collectors.toList());
    }
}

