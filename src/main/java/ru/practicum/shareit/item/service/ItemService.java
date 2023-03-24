package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;

public interface ItemService {
    List<ItemDtoOut> getUserItems(Integer userId);

    ItemDtoOut getItem(Integer itemId, Integer userId);

    ItemDtoOut create(Integer userId, ItemDtoIn itemDtoIn);

    ItemDtoOut update(Integer userId, ItemDtoIn itemDtoIn, Integer itemId);

    List<ItemDtoOut> searchItem(String request);

    void delete(Integer id);

    CommentDto addComment(CommentDtoIn commentDtoIn, Integer userId, Integer itemId);

}
