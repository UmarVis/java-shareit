package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoIn;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ItemRequestDtoOut {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private Set<ItemDtoIn> items;
}
