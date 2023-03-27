package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {
    public static Booking makeBooking(BookingDtoIn dtoIn) {
        Booking booking = new Booking();
        booking.setStart(dtoIn.getStart());
        booking.setEnd(dtoIn.getEnd());
        return booking;
    }

    public static BookingDtoOut makeBookingDtoOut(Booking booking) {
        BookingDtoOut dto = new BookingDtoOut();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setItem(new BookingDtoOut.ItemDto(booking.getItem().getId(), booking.getItem().getName()));
        dto.setBooker(new BookingDtoOut.UserDto(booking.getBooker().getId(), booking.getBooker().getName()));
        return dto;
    }

    public static BookingDtoShortOut makeBookingDtoShort(Booking booking) {
        BookingDtoShortOut out = new BookingDtoShortOut();
        out.setId(booking.getId());
        out.setStart(booking.getStart());
        out.setEnd(booking.getEnd());
        out.setBookerId(booking.getBooker().getId());
        return out;
    }

    public static List<BookingDtoOut> toListBookingDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::makeBookingDtoOut).collect(Collectors.toList());
    }
}
