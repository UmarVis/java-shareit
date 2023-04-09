package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingDtoIn;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoInTest {
    @Autowired
    JacksonTester<BookingDtoIn> jacksonTester;

    @Test
    void itemDtoOutTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        BookingDtoIn bookingDtoIn = new BookingDtoIn(1, now, now.plusHours(1));

        JsonContent<BookingDtoIn> result = jacksonTester.write(bookingDtoIn);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
    }
}
