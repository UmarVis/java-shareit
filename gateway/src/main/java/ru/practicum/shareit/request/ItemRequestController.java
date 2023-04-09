package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @NotNull Integer requesterId,
                                             @Valid @RequestBody ItemRequestDtoIn dtoIn) {
        log.info("Добавлен request {} с юзер ИД {}", dtoIn.getDescription(), requesterId);
        return requestClient.create(requesterId, dtoIn);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") @NotNull Integer requesterId) {
        log.info("Получен запрос с юзер ИД {}", requesterId);
        return requestClient.getRequestsByUser(requesterId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
                                                 @PathVariable Integer requestId) {
        log.info("Получен запрос {} для юзер с ИД {}", requestId, userId);
        return requestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получены все вещи с юзер ИД {}", userId);
        return requestClient.getAll(userId, from, size);
    }
}
