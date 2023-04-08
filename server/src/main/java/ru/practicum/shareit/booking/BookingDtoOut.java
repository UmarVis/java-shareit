package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoOut {
    private Integer id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private ItemDto item;
    private UserDto booker;

    @Data
    public static class ItemDto {
        private final Integer id;
        private final String name;
    }

    @Data
    public static class UserDto {
        private final Integer id;
        private final String name;
    }
}
