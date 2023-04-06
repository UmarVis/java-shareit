package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingDtoIn;
import ru.practicum.shareit.booking.BookingDtoOut;
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
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemService itemService;
    @Mock
    UserService userService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;

    private final LocalDateTime now = LocalDateTime.now();
    private final BookingDtoIn bookingDtoIn = new BookingDtoIn(1, now.plusHours(1), now.plusHours(2));
    private final User user = new User(1, "name", "email@mail.ru");
    private final Item item = new Item(1, "name", "desc", true, user, null,
            null, Set.of(), 1);
    private final ItemDtoOut itemDtoOut = new ItemDtoOut(1, "name", "desc", true, Set.of(), null,
            null, 1, 1);
    private final BookingDtoOut bookingDtoOut = new BookingDtoOut(1, now.plusHours(1), now.plusHours(2), Status.APPROVED,
            new BookingDtoOut.ItemDto(1, "name"), new BookingDtoOut.UserDto(1, "name"));
    private final Booking booking = new Booking(1, item, user, now.plusHours(1), now.plusHours(2), Status.APPROVED);

    @Test
    void addBookingTest() throws Exception {
        User userNew = new User(2, "nameNew", "email2@mail.ru");
        when(userService.getById(anyInt())).thenReturn(UserMapper.makeUserDto(userNew));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoOut bookingDtoOutReturned = bookingService.addBooking(1, bookingDtoIn);

        assertEquals(bookingDtoOut, bookingDtoOutReturned);
        assertEquals(bookingDtoOut.getItem(), bookingDtoOutReturned.getItem());
        System.out.println(bookingDtoOutReturned);
    }

    @Test
    void addBookingItemNotFoundTest() throws Exception {
        User userNew = new User(2, "nameNew", "email2@mail.ru");
        when(userService.getById(anyInt())).thenReturn(UserMapper.makeUserDto(userNew));

        ItemException e = assertThrows(ItemException.class, () -> bookingService.addBooking(user.getId(), bookingDtoIn));

        assertEquals("Вещь не найдена", e.getMessage());
    }

    @Test
    void addBookingOwnerEqualsUserTest() throws Exception {
        when(userService.getById(anyInt())).thenReturn(UserMapper.makeUserDto(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> bookingService.addBooking(user.getId(), bookingDtoIn));

        assertEquals("Owner cant booking his own item", e.getMessage());
    }

    @Test
    void addBookingExceptionTest() throws Exception {
        User userNew = new User(2, "nameNew", "email2@mail.ru");
        BookingDtoIn bookingDtoInNew = new BookingDtoIn(1, now.plusHours(3), now.plusHours(2));

        when(userService.getById(anyInt())).thenReturn(UserMapper.makeUserDto(userNew));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        BadRequestException e = assertThrows(BadRequestException.class, () -> bookingService.addBooking(user.getId(), bookingDtoInNew));

        assertEquals("Item is not available", e.getMessage());
    }

    @Test
    void approveTest() throws Exception {
        Booking bookingNew = new Booking(1, item, user, now.plusHours(1), now.plusHours(2), Status.WAITING);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(bookingNew));
        when(userService.getById(anyInt())).thenReturn(UserMapper.makeUserDto(user));
        when(itemService.getUserItems(anyInt())).thenReturn(List.of(itemDtoOut));
        when(itemService.getItem(anyInt(), anyInt())).thenReturn(itemDtoOut);

        BookingDtoOut bookingDtoOutReturned = bookingService.approve(1, 1, true);

        assertEquals(booking.getId(), bookingDtoOutReturned.getId());
        assertEquals(Status.APPROVED, bookingDtoOutReturned.getStatus());
    }

    @Test
    void approveBookingNotFoundTest() throws Exception {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        BookingException e = assertThrows(BookingException.class, () -> bookingService.approve(1, 1, true));

        assertEquals("Booking with id: 1 not found", e.getMessage());
    }

    @Test
    void approveStatusApprovedTest() throws Exception {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(userService.getById(anyInt())).thenReturn(UserMapper.makeUserDto(user));

        BadRequestException e = assertThrows(BadRequestException.class, () -> bookingService.approve(1, 1, true));

        assertEquals("Booking already approved", e.getMessage());
    }

    @Test
    void getByIdTest() throws Exception {
        when(userService.getById(anyInt())).thenReturn(UserMapper.makeUserDto(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        BookingDtoOut bookingDtoOutReturned = bookingService.getById(1, 1);

        assertEquals(booking.getId(), bookingDtoOutReturned.getId());
        assertEquals(Status.APPROVED, bookingDtoOutReturned.getStatus());
    }

    @Test
    void getByIdExceptionTest() throws Exception {
        when(userService.getById(anyInt())).thenReturn(UserMapper.makeUserDto(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        BookingException e = assertThrows(BookingException.class, () -> bookingService.getById(2, 1));

        assertEquals("User: 2 not owns booking with id: 1", e.getMessage());
    }

    @Test
    void getAllByUserAllTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBooker(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByUser(user.getId(), String.valueOf(State.ALL), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByUserCurrentTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(any(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByUser(user.getId(), String.valueOf(State.CURRENT), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByUserPastTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndEndBefore(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByUser(user.getId(), String.valueOf(State.PAST), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByUserFutureTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndStartAfter(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByUser(user.getId(), String.valueOf(State.FUTURE), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByUserWaitingTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusEquals(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByUser(user.getId(), String.valueOf(State.WAITING), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByUserRejectedTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusEquals(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByUser(user.getId(), String.valueOf(State.REJECTED), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByOwnerUserNotFoundTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> bookingService.getAllByOwner(1, String.valueOf(State.ALL), 0, 1));

        assertEquals("User с ИД 1 не найден", e.getMessage());
    }

    @Test
    void getAllByOwnerAllTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwner(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByOwner(user.getId(), String.valueOf(State.ALL), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByOwnerPastTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndEndBefore(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByOwner(user.getId(), String.valueOf(State.PAST), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByOwnerFutureTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStartAfter(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByOwner(user.getId(), String.valueOf(State.FUTURE), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByOwnerCurrentTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(any(), any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByOwner(user.getId(), String.valueOf(State.CURRENT), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByOwnerWaitingTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStatusEquals(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByOwner(user.getId(), String.valueOf(State.WAITING), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }

    @Test
    void getAllByOwnerRejectedTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerAndStatusEquals(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoOut> actualBookings = bookingService.getAllByOwner(user.getId(), String.valueOf(State.REJECTED), 0, 1);

        assertFalse(actualBookings.isEmpty());
        assertEquals(1, actualBookings.size());
        assertEquals(List.of(bookingDtoOut), actualBookings);
    }
}
