package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingServiceImpl bookingService;

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
        return bookingService.getById(userId,bookingId);
    }

    @GetMapping()
    public List<BookingDtoOut> getAllByUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllByOwner(userId, state);
    }
}
