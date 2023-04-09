package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    private final User user = new User(1, "testName", "testEmail@mail.ru");
    private final UserDto userDto = new UserDto(1, "testName", "testEmail@mail.ru");

    @Test
    void makeUserTest() throws Exception {
        User userNew = UserMapper.makeUser(userDto);
        assertEquals(userNew.getId(), userDto.getId());
        assertEquals(userNew.getName(), userDto.getName());
        assertEquals(userNew.getEmail(), userDto.getEmail());
    }

    @Test
    void makeUserDtoTest() throws Exception {
        UserDto userDtoNew = UserMapper.makeUserDto(user);
        assertEquals(userDtoNew.getId(), user.getId());
        assertEquals(userDtoNew.getName(), user.getName());
        assertEquals(userDtoNew.getEmail(), user.getEmail());
    }
}
