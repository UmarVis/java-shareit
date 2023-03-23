package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getUserItems(Integer userId);

    ItemDto getItem(Integer itemId, Integer userId);

    ItemDto create(Integer userId, ItemDto itemDto);

    ItemDto update(Integer userId, ItemDto itemDto, Integer itemId);

    List<ItemDto> searchItem(String request);

    void delete(Integer id);

    CommentDto addComment(CommentDtoIn commentDtoIn, Integer userId, Integer itemId);

}
