package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody @Valid BookingDtoIn bookingDtoIn) {
        log.info("Добавлен букинг {}, с юзер ИД = {}", bookingDtoIn, userId);
        return bookingClient.addBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @PathVariable Integer bookingId,
                                          @RequestParam Boolean approved) {
        log.info("Approving booking {}, with {}, by owner {}", bookingId, approved, userId);
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @PathVariable Integer bookingId) {
        log.info("Получен букинг {}, с юзер ИД = {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("getAllByUser with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("getAllByOwner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }
}
