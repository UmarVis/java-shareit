package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void testUserDto() throws IOException {
        UserDto userDto = new UserDto(1, "testName", "testEmail@mail.ru");

        JsonContent<UserDto> result = jacksonTester.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("testName");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("testEmail@mail.ru");
    }
}
