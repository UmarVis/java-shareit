package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getUserItems(Integer userId);

    ItemDto getById(Integer itemId);

    ItemDto create(Integer userId, ItemDto itemDto);

    ItemDto update(Integer userId, ItemDto itemDto, Integer itemId);

    List<ItemDto> searchItem(String request);

    void delete(Integer id);
}
