package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingDtoShortOut;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoOut {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Set<CommentDto> comments;
    private BookingDtoShortOut lastBooking;
    private BookingDtoShortOut nextBooking;
    private Integer owner;
    private Integer requestId;

    @Data
    public static class BookingDtoOut {
        private final Integer id;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final Integer bookerId;
    }
}
