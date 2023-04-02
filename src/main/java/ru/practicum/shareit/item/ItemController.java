package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDtoOut create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @RequestBody @Validated(Create.class) ItemDtoIn itemDtoIn) {
        return itemService.create(userId, itemDtoIn);
    }

    @GetMapping("{id}")
    public ItemDtoOut getItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer id) {
        return itemService.getItem(id, userId);
    }

    @GetMapping
    public List<ItemDtoOut> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getUserItems(userId);
    }

    @PatchMapping("{id}")
    public ItemDtoOut update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @RequestBody @Validated(Update.class) ItemDtoIn itemDtoIn, @PathVariable Integer id) {
        return itemService.update(userId, itemDtoIn, id);
    }

    @GetMapping("/search")
    public List<ItemDtoOut> searchItem(@RequestParam(name = "text") String word) {
        return itemService.searchItem(word);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        itemService.delete(id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer itemId,
                                 @Valid @RequestBody CommentDtoIn commentDtoIn) {
        return itemService.addComment(commentDtoIn, userId, itemId);
    }
}
