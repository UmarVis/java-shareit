package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoInTest {
    @Autowired
    JacksonTester<ItemRequestDtoIn> jacksonTester;

    @Test
    void itemRequestDtoInTest() throws Exception {
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn("test");

        JsonContent<ItemRequestDtoIn> result = jacksonTester.write(itemRequestDtoIn);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
    }
}
