package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        User user = userRepository.save(UserMapper.makeUser(userDto));
        log.info("Пользователь с именем {} создан", user.getName());
        return UserMapper.makeUserDto(user);
    }

    @Override
    public UserDto getById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Пользователь с ИД " + id + " не найден"));
        log.info("Получен пользователь с ИД {}", id);
        return UserMapper.makeUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Integer id) {
        User updateUser = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("Пользователь с ИД " + id + " не найден"));
        if (userDto.getName() != null && !(userDto.getName().isBlank())) {
            updateUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !(userDto.getEmail().isBlank())) {
            updateUser.setEmail(userDto.getEmail());
        }
        log.info("Полтьователь с ИД {} обновлен", id);
        return UserMapper.makeUserDto(updateUser);
    }

    @Override
    @Transactional
    public UserDto delete(Integer id) {
        UserDto userDto = getById(id);
        userRepository.deleteById(id);
        log.info("Пользователь с ИД {} удален", id);
        return userDto;
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::makeUserDto).collect(Collectors.toList());
    }
}

