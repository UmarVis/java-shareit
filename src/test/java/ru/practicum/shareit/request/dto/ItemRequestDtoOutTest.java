package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemDtoIn;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest

public class ItemRequestDtoOutTest {
    @Autowired
    JacksonTester<ItemRequestDtoOut> jacksonTester;

    @Test
    void itemRequestDtoOutTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemDtoIn itemDtoIn = ItemDtoIn.builder().id(1).name("itemTest").description("itemDesc").available(true)
                .requestId(1).build();
        ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut(1, "test", now, Set.of(itemDtoIn));

        JsonContent<ItemRequestDtoOut> result = jacksonTester.write(itemRequestDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
        assertThat(result).extractingJsonPathArrayValue("$.items").isNotEmpty();
        assertThat(result).extractingJsonPathNumberValue("$.items.size()").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items.[0].name").isEqualTo("itemTest");
        assertThat(result).extractingJsonPathStringValue("$.items.[0].description").isEqualTo("itemDesc");
        assertThat(result).extractingJsonPathBooleanValue("$.items.[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].requestId").isEqualTo(1);

    }
}
