package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingDtoOut;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoOutTest {
    @Autowired
    JacksonTester<BookingDtoOut> jacksonTester;

    @Test
    void itemDtoOutTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        BookingDtoOut bookingDtoOut = new BookingDtoOut(1, now, now.plusHours(1), null,
                new BookingDtoOut.ItemDto(1, "name"), new BookingDtoOut.UserDto(1, "name"));

        JsonContent<BookingDtoOut> result = jacksonTester.write(bookingDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("name");
    }
}
