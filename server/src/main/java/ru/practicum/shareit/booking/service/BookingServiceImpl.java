package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.BookingDtoOut;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.ItemException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDtoOut addBooking(Integer userId, BookingDtoIn dtoIn) {
        User user = UserMapper.makeUser(userService.getById(userId));
        Item item = itemRepository.findById(dtoIn.getItemId()).orElseThrow(() ->
                new ItemException("Вещь не найдена"));
        Booking booking = BookingMapper.makeBooking(dtoIn);
        if (item.getOwner().getId().equals(user.getId())) {
            throw new UserNotFoundException("Owner cant booking his own item");
        }
        if (!item.getAvailable() || !dtoIn.getStart().isBefore(dtoIn.getEnd())) {
            throw new BadRequestException("Item is not available");
        }
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        return BookingMapper.makeBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoOut approve(Integer userId, Integer bookingId, Boolean approved) {
        Booking booking = bookingOrException(bookingId);
        userService.getById(userId);
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException("Booking already approved");
        }
        if (approved && ownerCheck(userId, bookingId)) {
            booking.setStatus(Status.APPROVED);
        } else if (!approved && ownerCheck(userId, bookingId)) {
            booking.setStatus(Status.REJECTED);
        } else {
            throw new BookingException("Only owner approves booking");
        }
        return BookingMapper.makeBookingDtoOut(booking);
    }

    @Override
    public BookingDtoOut getById(Integer userId, Integer bookingId) {
        userService.getById(userId);
        Booking booking = bookingOrException(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !ownerCheck(userId, bookingId)) {
            throw new BookingException("User: " + userId + " not owns booking with id: " + bookingId);
        }
        return BookingMapper.makeBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> getAllByUser(Integer userId, String state, int from, int size) {
        final State stateIn = getState(state);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_START_DESC);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден"));
        List<Booking> bookings = List.of();
        switch (stateIn) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(user, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusEquals(userId, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusEquals(userId, Status.REJECTED, pageable);
                break;
        }
        return BookingMapper.toListBookingDto(bookings);
    }

    @Override
    public List<BookingDtoOut> getAllByOwner(Integer userId, String state, int from, int size) {
        final State stateIn = getState(state);
        Pageable pageable = PageRequest.of(from / size, size, SORT_BY_START_DESC);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User с ИД " + userId + " не найден"));
        List<Booking> bookings = List.of();
        switch (stateIn) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwner(user, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, pageable);
                break;
        }
        return BookingMapper.toListBookingDto(bookings);
    }

    private Booking bookingOrException(Integer bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingException("Booking with id: " + bookingId + " not found"));
    }

    private Boolean ownerCheck(Integer userId, Integer bookingId) {
        List<ItemDtoOut> listDto = itemService.getUserItems(userId);
        Booking booking = bookingOrException(bookingId);
        if (!listDto.contains(itemService.getItem(booking.getItem().getId(), userId))) {
            return false;
        }
        return true;
    }

    private State getState(String state) {
        try {
            return State.valueOf(state);
        } catch (Throwable e) {
            throw new BadRequestException("Unknown state: " + state);
        }
    }
}
