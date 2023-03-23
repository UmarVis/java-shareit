package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDtoShortOut {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer bookerId;
}