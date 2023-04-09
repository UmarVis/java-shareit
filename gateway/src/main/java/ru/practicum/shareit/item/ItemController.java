package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.validate.Create;
import ru.practicum.shareit.validate.Update;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor
@RestController
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
                                         @RequestBody @Validated({Create.class}) ItemDtoIn itemDtoIn) {
        log.info("Добавлен item {} с юзер ИД {}", itemDtoIn.getName(), userId);
        return itemClient.create(itemDtoIn, userId);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @PathVariable Integer id) {
        log.info("Получен item {} с юзер ИД {}", id, userId);
        return itemClient.getItem(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        log.info("Получены вещи для юзер ИД {}", userId);
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam(name = "text") String word) {
        if (word.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        log.info("Поиск вещей со словом {}", word);
        return itemClient.searchItem(word);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @RequestBody @Validated(Update.class) ItemDtoIn itemDtoIn, @PathVariable Integer id) {
        log.info("Обновлена вещь с ИД {} с описанием {} с юзер ИД {}", id, itemDtoIn.getDescription(), userId);
        return itemClient.updateItem(itemDtoIn, id, userId);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Вещь с ИД {} удалена", id);
        itemClient.delete(id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer itemId,
                                             @Valid @RequestBody CommentDtoIn commentDtoIn) {
        log.info("Добавлен комментарий {} для вещи {} с юзер ИД {}", commentDtoIn.getText(), itemId, userId);
        return itemClient.addComment(commentDtoIn, userId, itemId);
    }
}
