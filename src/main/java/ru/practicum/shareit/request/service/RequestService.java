package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface RequestService {
    ItemRequestDtoOut create(Integer userId, ItemRequestDtoIn itemRequestDtoIn);
    List<ItemRequestDtoOut> getRequestByUser(Integer userId);
    ItemRequestDtoOut getById(Integer userId, Integer id);
    List<ItemRequestDtoOut> getAll(Integer userId, int from, int size);
}
