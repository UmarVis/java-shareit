package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validate.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoIn {
    private Integer id;
    @NotBlank(groups = {Create.class})
    private String name;
    @NotBlank(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private Integer requestId;
}
