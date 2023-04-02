package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingDtoShortOut;
import ru.practicum.shareit.item.ItemDtoOut;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoOutTest {
    @Autowired
    JacksonTester<ItemDtoOut> jacksonTester;

    @Test
    void ItemDtoOutTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingDtoShortOut lastBooking = new BookingDtoShortOut(1, now, now.plusHours(1), 2);
        ItemDtoOut itemDtoOut = new ItemDtoOut(1, "name", "description", true, null,
                lastBooking, null, 1, 1);

        JsonContent<ItemDtoOut> result = jacksonTester.write(itemDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.comments").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}
