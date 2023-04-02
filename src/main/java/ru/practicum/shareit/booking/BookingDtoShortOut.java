package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoShortOut {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer bookerId;
}