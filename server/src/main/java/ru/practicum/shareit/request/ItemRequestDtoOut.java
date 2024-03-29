package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDtoIn;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDtoOut {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private Set<ItemDtoIn> items;
}
