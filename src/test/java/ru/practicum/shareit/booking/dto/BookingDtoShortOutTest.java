package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingDtoShortOut;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoShortOutTest {
    @Autowired
    JacksonTester<BookingDtoShortOut> jacksonTester;

    @Test
    void ItemDtoOutTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        BookingDtoShortOut bookingDtoShortOut = new BookingDtoShortOut(1, now, now.plusHours(1), 2);

        JsonContent<BookingDtoShortOut> result = jacksonTester.write(bookingDtoShortOut);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
    }
}
