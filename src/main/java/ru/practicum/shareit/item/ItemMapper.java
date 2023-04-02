package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentMapper commentMapper;

    public ItemDtoOut makeItemDto(Item item) {
        return ItemDtoOut.builder().id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequester())
                .comments(item.getComments() != null ? toListCommentDto(item.getComments()) : null)
                .lastBooking(item.getLastBooking() != null ? BookingMapper.makeBookingDtoShort(item.getLastBooking()) : null)
                .nextBooking(item.getNextBooking() != null ? BookingMapper.makeBookingDtoShort(item.getNextBooking()) : null)
                .build();
    }

    public Item makeItem(ItemDtoIn itemDtoIn) {
        return Item.builder().id(itemDtoIn.getId())
                .name(itemDtoIn.getName())
                .description(itemDtoIn.getDescription())
                .available(itemDtoIn.getAvailable())
                .requester(itemDtoIn.getRequestId())
                .build();
    }

    public List<ItemDtoOut> toListDto(List<Item> items) {
        return items.stream().map(this::makeItemDto).collect(Collectors.toList());
    }

    public Set<ItemDtoIn> makeSetItemShortDto(Set<Item> items) {
        return items.stream().map(this::makeItemIn).collect(Collectors.toSet());
    }

    public ItemDtoIn makeItemIn(Item item) {
        return ItemDtoIn.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequester() != null ? item.getRequester() : null)
                .build();
    }

    public Set<CommentDto> toListCommentDto(Set<Comment> comments) {
        return comments.stream().map(commentMapper::makeCommentDto).collect(Collectors.toSet());
    }
}
