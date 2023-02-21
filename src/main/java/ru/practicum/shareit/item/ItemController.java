package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @RequestBody @Validated(Create.class) ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @GetMapping("{id}")
    public ItemDto getById(@PathVariable Integer id) {
        return itemService.getById(id);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getUserItems(userId);
    }

    @PatchMapping("{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @RequestBody @Validated(Update.class) ItemDto itemDto, @PathVariable Integer id) {
        return itemService.update(userId, itemDto, id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String word) {
        if (word.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.searchItem(word);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        itemService.delete(id);
    }
}
