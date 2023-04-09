package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemDtoIn;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoInTest {
    @Autowired
    JacksonTester<ItemDtoIn> jacksonTester;

    @Test
    void dtoInTest() throws Exception {

        ItemDtoIn itemDtoIn = new ItemDtoIn(1, "name", "description", true, 1);

        JsonContent<ItemDtoIn> result = jacksonTester.write(itemDtoIn);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}
