package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {
    private final LocalDateTime now = LocalDateTime.now();
    private final User user = new User(1, "name", "mail@mail.ru");
    private final ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn("desc");
    private final ItemRequest itemRequest = new ItemRequest(1, "desc", user, now, Set.of());
    private final ItemRequest itemRequestWithItems = new ItemRequest(1, "desc", user, now,
            Set.of(new Item(1, "items", "desc", true, user, null, null, Set.of(), 1)));
    private final RequestMapper requestMapper = new RequestMapper(new ItemMapper(new CommentMapper()));

    @Test
    void makeItemRequestTest() {
        var result = requestMapper.makeItemRequest(itemRequestDtoIn);
        assertEquals(itemRequest.getDescription(), result.getDescription());
    }

    @Test
    void makeItemRequestDtoOutTest() {
        var result = requestMapper.makeItemRequestDtoOut(itemRequest);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
    }

    @Test
    void makeItemRequestDtoWithItemsTest() {
        var result = requestMapper.makeItemRequestDtoWithItems(itemRequestWithItems);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
    }
}
