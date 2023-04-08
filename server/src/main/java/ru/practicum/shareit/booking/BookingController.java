package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingDtoOut addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                    @RequestBody BookingDtoIn dtoIn) {
        return bookingService.addBooking(userId, dtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approve(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer bookingId,
                                 @RequestParam Boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @PathVariable Integer bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDtoOut> getAllByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(value = "from", defaultValue = "0") int from,
                                            @RequestParam(value = "size", defaultValue = "5") int size) {
        return bookingService.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(value = "from", defaultValue = "0") int from,
                                             @RequestParam(value = "size", defaultValue = "5") int size) {
        return bookingService.getAllByOwner(userId, state, from, size);
    }
}
