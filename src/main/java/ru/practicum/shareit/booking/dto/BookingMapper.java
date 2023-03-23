package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {
    public Booking makeBooking(BookingDtoIn dtoIn) {
        Booking booking = new Booking();
        booking.setStart(dtoIn.getStart());
        booking.setEnd(dtoIn.getEnd());
        return booking;
    }

    public BookingDtoOut makeBookingDtoOut(Booking booking) {
        BookingDtoOut dto = new BookingDtoOut();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setItem(new BookingDtoOut.ItemDto(booking.getItem().getId(), booking.getItem().getName()));
        dto.setBooker(new BookingDtoOut.UserDto(booking.getBooker().getId(), booking.getBooker().getName()));
        return dto;
    }

    public BookingDtoShortOut makeBookingDtoShort(Booking booking) {
        BookingDtoShortOut out = new BookingDtoShortOut();
        out.setId(booking.getId());
        out.setStart(booking.getStart());
        out.setEnd(booking.getEnd());
        out.setBookerId(booking.getBooker().getId());
        return out;
    }
}
