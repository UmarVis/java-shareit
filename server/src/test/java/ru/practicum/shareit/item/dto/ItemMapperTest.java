package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {
    private final ItemMapper itemMapper = new ItemMapper(new CommentMapper());
    private final LocalDateTime now = LocalDateTime.now();
    private final User user = new User(1, "name", "name@mail.ru");
    private final Item item = new Item(1, "name", "desc", true, user, null, null, Set.of(), 1);
    private final ItemDtoIn itemDtoIn = new ItemDtoIn(1, "name", "desc", true, 1);
    private final Comment comment = new Comment(1, "text", item, user, now);

    @Test
    void makeItemDtoTest() {
        var result = itemMapper.makeItemDto(item);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        assertEquals(result.getRequestId(), item.getOwner().getId());
    }

    @Test
    void makeItemTest() {
        var result = itemMapper.makeItem(itemDtoIn);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        assertEquals(result.getRequester(), item.getOwner().getId());
    }

    @Test
    void toListDtoTest() {
        List<ItemDtoOut> result = itemMapper.toListDto(List.of(item));
        assertEquals(result.get(0).getId(), item.getId());
        assertEquals(result.get(0).getName(), item.getName());
        assertEquals(result.get(0).getDescription(), item.getDescription());
        assertEquals(result.get(0).getAvailable(), item.getAvailable());
    }

    @Test
    void makeSetItemShortDtoTest() {
        Set<ItemDtoIn> result = itemMapper.makeSetItemShortDto(Set.of(item));
        assertEquals(result, Set.of(itemDtoIn));
    }

    @Test
    void makeItemInTest() {
        var result = itemMapper.makeItemIn(item);
        assertEquals(result.getId(), item.getId());
        assertEquals(result.getName(), item.getName());
        assertEquals(result.getDescription(), item.getDescription());
        assertEquals(result.getAvailable(), item.getAvailable());
        assertEquals(result.getRequestId(), item.getOwner().getId());
    }

    @Test
    void toListCommentDtoTest() {
        CommentDto commentDto = new CommentDto(1, "text", "name", now);
        Set<CommentDto> result = itemMapper.toListCommentDto(Set.of(comment));
        assertEquals(result, Set.of(commentDto));
    }
}
