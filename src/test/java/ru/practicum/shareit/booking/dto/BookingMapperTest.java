package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingDtoShortOut;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {
    private final LocalDateTime now = LocalDateTime.now();
    private final User user = new User(1, "name", "name@mail.ru");
    private final Item item = new Item(1, "name", "desc", true, user, null, null, Set.of(), 1);
    private final Booking booking = new Booking(1, item, user, now, now.plusHours(1), Status.APPROVED);

    @Test
    void makeBookingDtoShortTest() {
        BookingDtoShortOut result = BookingMapper.makeBookingDtoShort(booking);
        assertEquals(result.getId(), booking.getId());
        assertEquals(result.getStart(), booking.getStart());
        assertEquals(result.getEnd(), booking.getEnd());
        assertEquals(result.getBookerId(), booking.getBooker().getId());

    }
}