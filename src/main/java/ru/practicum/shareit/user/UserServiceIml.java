package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceIml implements UserService {
    private final UserRepository userRepository;

    public UserDto add(UserDto userDto) {
        return makeUserDto(userRepository.add(makeUser(userDto)));
    }

    public UserDto getById(Integer id) {
        try {
            userRepository.getById(id);
        } catch (NullPointerException exception) {
            throw new UserNotFoundException(String.format("User %s not found", id));
        }
        return makeUserDto(userRepository.getById(id));
    }

    public UserDto update(UserDto userDto, Integer id) {
        checkEmail(userDto, id);
        try {
            userRepository.getById(id).getId();
        } catch (NullPointerException e) {
            throw new UserNotFoundException(String.format("User %s not found", id));
        }
        User user = userRepository.getById(id);
        userRepository.delete(id);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return makeUserDto(userRepository.update(user, id));
    }

    public UserDto delete(Integer id) {
        try {
            userRepository.getById(id);
        } catch (NullPointerException e) {
            throw new UserNotFoundException(String.format("User %s not found", id));
        }
        return makeUserDto(userRepository.delete(id));
    }

    public List<UserDto> getAll() {
        List<UserDto> usersList = new ArrayList<>();
        for (User users : userRepository.getAll()) {
            usersList.add(makeUserDto(users));
        }
        return usersList;
    }

    private User makeUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    private UserDto makeUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private void checkEmail(UserDto userDto, Integer id) {
        List<UserDto> userWithSameEmail = getAll()
                .stream()
                .filter(u -> u.getEmail().equals(userDto.getEmail()))
                .filter(u -> !Objects.equals(u.getId(), id))
                .collect(Collectors.toList());

        if (!userWithSameEmail.isEmpty()) {
            throw new ValidationException("Пользователь с такой почтой уже есть");
        }
    }
}

