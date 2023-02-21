package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceIml implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto add(UserDto userDto) {
        return mapper.makeUserDto(userRepository.add(mapper.makeUser(userDto)));
    }

    @Override
    public UserDto getById(Integer id) {
        return mapper.makeUserDto(userRepository.getById(id));
    }

    @Override
    public UserDto update(UserDto userDto, Integer id) {
        userRepository.checkEmail(mapper.makeUser(userDto), id);
        User user = userRepository.getById(id);
        if (userDto.getName() != null && !(userDto.getName().isBlank())) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !(userDto.getEmail().isBlank())) {
            user.setEmail(userDto.getEmail());
        }
        return mapper.makeUserDto(user);
    }

    @Override
    public UserDto delete(Integer id) {
        userRepository.getById(id);
        return mapper.makeUserDto(userRepository.delete(id));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream().map(mapper::makeUserDto).collect(Collectors.toList());
    }
}

