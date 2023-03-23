package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final static Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDtoOut addBooking(Integer userId, BookingDtoIn dtoIn) {
        User user = userMapper.makeUser(userService.getById(userId));
        Item item = itemRepository.findById(dtoIn.getItemId()).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден"));
        Booking booking = bookingMapper.makeBooking(dtoIn);
        if (item.getOwner().getId().equals(user.getId())) {
            throw new UserNotFoundException("Owner cant booking his own item");
        }
        if (!item.getAvailable() || dtoIn.getStart().isAfter(dtoIn.getEnd())
                || dtoIn.getStart().equals(dtoIn.getEnd())) {
            throw new BadRequestException("Item is not available");
        }
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        return bookingMapper.makeBookingDtoOut(bookingRepository.save(booking));
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
        return bookingMapper.makeBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoOut getById(Integer userId, Integer bookingId) {
        userService.getById(userId);
        Booking booking = bookingOrException(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !ownerCheck(userId, bookingId)) {
            throw new BookingException("User: " + userId + " not owns booking with id: " + bookingId);
        }
        return bookingMapper.makeBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> getAllByUser(Integer userId, String state) {
        final State stateIn = getState(state);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден"));
        List<Booking> bookings = List.of();
        switch (stateIn) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(user, SORT_BY_START_DESC);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusEquals(userId, Status.WAITING, SORT_BY_START_DESC);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusEquals(userId, Status.REJECTED, SORT_BY_START_DESC);
                break;
        }
        return toListBookingDto(bookings);
    }

    @Override
    public List<BookingDtoOut> getAllByOwner(Integer userId, String state) {
        final State stateIn = getState(state);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User с ИД " + userId + " не найден"));
        List<Booking> bookings = List.of();
        switch (stateIn) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwner(user, SORT_BY_START_DESC);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, SORT_BY_START_DESC);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, SORT_BY_START_DESC);
                break;
        }
        return toListBookingDto(bookings);
    }

    private Booking bookingOrException(Integer bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingException("Booking with id: " + bookingId + " not found"));
    }

    private Boolean ownerCheck(Integer userId, Integer bookingId) {
        List<ItemDto> listDto = itemService.getUserItems(userId);
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

    private List<BookingDtoOut> toListBookingDto(List<Booking> bookings) {
        return bookings.stream().map(bookingMapper::makeBookingDtoOut).collect(Collectors.toList());
    }
}
