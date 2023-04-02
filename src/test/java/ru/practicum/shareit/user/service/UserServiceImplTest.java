package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;

    private final User user = new User(1, "nameTest", "emailTest@mail.ru");
    private final UserDto userDto = new UserDto(1, "nameDtoTest", "emailDtoTest@mail.ru");

    @Test
    void add_CheckReturnUser() {
        when(userRepository.save(any()))
                .thenReturn(UserMapper.makeUser(userDto));

        UserDto saveUserDto = userService.add(userDto);

        assertEquals(userDto, saveUserDto);
        verify(userRepository).save(any());
    }

    @Test
    void getById_CheckReturnUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(UserMapper.makeUser(userDto)));

        UserDto getUserDto = userService.getById(anyInt());

        assertEquals(userDto, getUserDto);
    }

    @Test
    void getById_whenExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userService.getById(anyInt()));
        assertEquals("Пользователь с ИД 0 не найден", e.getMessage());
    }

    @Test
    void getAllTestOk() {
        when(userRepository.findAll()).thenReturn(List.of(UserMapper.makeUser(userDto)));

        List<UserDto> AllDtoUsers = userService.getAll();

        assertFalse(AllDtoUsers.isEmpty());
        assertEquals(1, AllDtoUsers.size());
        assertEquals(userDto, AllDtoUsers.get(0));
    }

    @Test
    void getAllTestEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> AllDtoUsers = userService.getAll();

        assertTrue(AllDtoUsers.isEmpty());
    }

    @Test
    void updateTestOk() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(UserMapper.makeUser(userDto)));

        UserDto saveUserDto = userService.update(userDto, userDto.getId());

        assertEquals(userDto.getName(), saveUserDto.getName());
        assertEquals(userDto.getEmail(), saveUserDto.getEmail());
    }

    @Test
    void updateTestWhenExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userService.update(userDto, userDto.getId()));
        assertEquals("Пользователь с ИД 1 не найден", e.getMessage());
    }

    @Test
    void deleteOk() {
        when(userRepository.findById(1))
                .thenReturn(Optional.of(UserMapper.makeUser(userDto)));

        userService.delete(1);
        verify(userRepository, times(1)).deleteById(1);
    }
}
