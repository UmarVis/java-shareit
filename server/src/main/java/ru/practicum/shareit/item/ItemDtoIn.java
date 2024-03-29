package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoIn {
    private Integer id;

    private String name;

    private String description;

    private Boolean available;
    private Integer requestId;
}
