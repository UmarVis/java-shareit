package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDtoShortOut;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ItemDto {
    private Integer id;
    @NotBlank(groups = {Create.class})
    private String name;
    @NotBlank(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private Set<CommentDto> comments;
    private BookingDtoShortOut lastBooking;
    private BookingDtoShortOut nextBooking;
    private Integer owner;

    @Data
    public static class BookingDtoOut {
        private final Integer id;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final Integer bookerId;
    }
}
