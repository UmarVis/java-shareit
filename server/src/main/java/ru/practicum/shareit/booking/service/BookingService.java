package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.BookingDtoOut;

import java.util.List;

public interface BookingService {
    BookingDtoOut addBooking(Integer userId, BookingDtoIn bookingDtoIn);

    BookingDtoOut approve(Integer userId, Integer bookingId, Boolean approved);

    BookingDtoOut getById(Integer userId, Integer bookingId);

    List<BookingDtoOut> getAllByUser(Integer userId, String state, int from, int size);

    List<BookingDtoOut> getAllByOwner(Integer userId, String state, int from, int size);
}
