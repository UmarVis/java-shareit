package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final ItemMapper itemMapper;

    public ItemRequest makeItemRequest(ItemRequestDtoIn itemRequestDtoIn) {
        return ItemRequest.builder()
                .description(itemRequestDtoIn.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public ItemRequestDtoOut makeItemRequestDtoOut(ItemRequest itemRequest) {
        return ItemRequestDtoOut.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequestDtoOut makeItemRequestDtoWithItems(ItemRequest itemRequest) {
        return ItemRequestDtoOut.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() != null ? itemMapper.makeSetItemShortDto(itemRequest.getItems()) : null)
                .build();
    }

    public List<ItemRequestDtoOut> makeListItemRequestDtoWithItems(List<ItemRequest> requests) {
        return requests.stream().map(this::makeItemRequestDtoWithItems).collect(Collectors.toList());
    }
}
