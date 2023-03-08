package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {
    public Booking makeBooking(BookingDtoIn dtoIn) {
        Booking booking = new Booking();
        booking.setItemId(dtoIn.getItemId());
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
        return dto;
    }
}
