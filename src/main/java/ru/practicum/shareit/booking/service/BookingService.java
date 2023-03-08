package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {
    BookingDtoOut addBooking(Integer userId, BookingDtoIn bookingDtoIn);

    BookingDtoOut approve(Integer userId, Integer bookingId, Boolean approved);

    BookingDtoOut getById(Integer userId, Integer bookingId);

    List<BookingDtoOut> getAllByUser(Integer userId, String state);

    List<BookingDtoOut> getAllByOwner(Integer userId, String state);
}
