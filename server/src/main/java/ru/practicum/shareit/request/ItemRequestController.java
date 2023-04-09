package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDtoOut create(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestBody @Validated ItemRequestDtoIn itemRequestDtoIn) {
        return requestService.create(userId, itemRequestDtoIn);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getRequestByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestService.getRequestByUser(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoOut getById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @PathVariable("requestId") Integer id) {
        return requestService.getById(userId, id);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @RequestParam(value = "from", defaultValue = "0") int from,
                                          @RequestParam(value = "size", defaultValue = "5") int size) {
        return requestService.getAll(userId, from, size);
    }
}
