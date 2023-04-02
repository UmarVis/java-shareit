package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingDtoShortOut;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemMapper itemMapper;

    @Mock
    RequestRepository requestRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    CommentMapper commentMapper;


    LocalDateTime now = LocalDateTime.now();
    private final User user = new User(1, "user", "user@mail.ru");
    private final ItemRequest itemRequest = new ItemRequest(1, "desc", user, now, Set.of());
    private final ItemDtoIn itemDtoIn = new ItemDtoIn(1, "name", "desc", true, 1);
    private final Item item = new Item(1, "name", "desc", true, user, null,
            null, null, 2);
    private final Comment comment = new Comment(1, "text", item, user, now);
    private final CommentDto commentDto = new CommentDto(1, "text", "autor", now);
    private final CommentDtoIn commentDtoIn = new CommentDtoIn("text");
    BookingDtoShortOut lastBooking = new BookingDtoShortOut(1, now, now.plusHours(1), 1);
    private final ItemRequestDtoOut itemRequestDtoOut = new ItemRequestDtoOut(1, "desc", now, null);
    private final ItemDtoOut itemDtoOut = new ItemDtoOut(1, "name", "description", true,
            Set.of(commentDto), lastBooking, null, 1, 1);
    private final Booking booking = new Booking(1, item, user, now, now.plusHours(1), Status.APPROVED);


    @Test
    void createTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemMapper.makeItem(any())).thenReturn(item);
        when(requestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));
        when(itemMapper.makeItemDto(any())).thenReturn(itemDtoOut);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDtoOut itemDtoOutReturn = itemService.create(user.getId(), itemDtoIn);

        assertEquals(itemDtoOut, itemDtoOutReturn);
        assertEquals(itemDtoOut.getDescription(), itemDtoOutReturn.getDescription());
    }

    @Test
    void createTestExceptionUser() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> itemService.create(user.getId(), itemDtoIn));

        assertEquals("User с ИД 1 не найден", e.getMessage());
    }

    @Test
    void createTestExceptionRequest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemMapper.makeItem(any())).thenReturn(item);
        when(requestRepository.findById(anyInt())).thenReturn(Optional.empty());

        ItemException e = assertThrows(ItemException.class, () -> itemService.create(user.getId(), itemDtoIn));

        assertEquals("Request with 2 id not found.", e.getMessage());
    }

    @Test
    void getUserItemsTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerOrderById(any())).thenReturn(List.of(item));
        when(bookingRepository.findByItemInAndStatusEqualsAndStartLessThanEqualOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findByItemInAndStatusEqualsAndStartAfterOrderByStart(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(itemMapper.toListDto(anyList())).thenReturn(List.of(itemDtoOut));
        when(commentRepository.findByItemIn(anyList())).thenReturn(Set.of(comment));

        List<ItemDtoOut> itemDtoOutList = itemService.getUserItems(1);

        assertEquals(1, itemDtoOutList.size());
        assertEquals(Set.of(commentDto), itemDtoOutList.get(0).getComments());
        assertEquals(lastBooking, itemDtoOutList.get(0).getLastBooking());
    }

    @Test
    void getUserItemUserExceptionTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        ;

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> itemService.getUserItems(user.getId()));

        assertEquals("User с ИД 1 не найден", e.getMessage());
    }

    @Test
    void getItemTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemInAndStatusEqualsAndStartLessThanEqualOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findByItemInAndStatusEqualsAndStartAfterOrderByStart(any(), any(), any()))
                .thenReturn(List.of(booking));
        when(commentRepository.findByItemIn(anyList())).thenReturn(Set.of(comment));
        when(itemMapper.makeItemDto(any())).thenReturn(itemDtoOut);

        ItemDtoOut itemDtoOutReturned = itemService.getItem(1, 1);

        assertEquals(itemDtoOut, itemDtoOutReturned);
        assertEquals(Set.of(commentDto), itemDtoOutReturned.getComments());
        assertEquals(lastBooking, itemDtoOutReturned.getLastBooking());
    }

    @Test
    void getItemUserExceptionTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> itemService.getItem(1, 1));

        assertEquals("User с ИД 1 не найден", e.getMessage());
    }

    @Test
    void getItemItemExceptionTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        ItemException e = assertThrows(ItemException.class, () -> itemService.getItem(1, 1));

        assertEquals("Вещь с ИД 1 не найдена", e.getMessage());
    }

    @Test
    void updateTest() throws Exception {
        ItemDtoIn newItemDtoIn = new ItemDtoIn(1, "name", "description", false, null);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemMapper.makeItemDto(any())).thenReturn(itemDtoOut);

        ItemDtoOut actualItem = itemService.update(1, newItemDtoIn, 1);

        assertEquals(newItemDtoIn.getName(), actualItem.getName());
        assertEquals(newItemDtoIn.getDescription(), actualItem.getDescription());
    }

    @Test
    void updateUserExceptionTest() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        ItemException e = assertThrows(ItemException.class, () -> itemService.update(1, itemDtoIn, 1));

        assertEquals("Вещь с ИД 1 не найдена", e.getMessage());

    }

    @Test
    void updateItemExceptionTest() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());


        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> itemService.update(1, itemDtoIn, 1));

        assertEquals("Пользователь с ИД 1 не найден", e.getMessage());

    }

    @Test
    void updateExceptionGetOwnerNotEqualsUser() throws Exception {
        User user1 = new User(2, "name1", "email1@mail.ru");
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1));


        ItemException e = assertThrows(ItemException.class, () -> itemService.update(1, itemDtoIn, 1));

        assertEquals("Item with id: 1 not belongs to user with id: 1", e.getMessage());

    }

    @Test
    void searchItemTest() throws Exception {
        when(itemRepository.findItemByText(anyString())).thenReturn(List.of(item));
        when(itemMapper.makeItemDto(any())).thenReturn(itemDtoOut);

        List<ItemDtoOut> itemDtoOutList = itemService.searchItem("name");

        assertEquals(1, itemDtoOutList.size());
    }

    @Test
    void searchItemBlankRequestTest() throws Exception {
        List<ItemDtoOut> itemDtoOutList = itemService.searchItem("");

        assertTrue(itemDtoOutList.isEmpty());
    }

    @Test
    void deleteTest() throws Exception {
        itemRepository.deleteById(1);
    }

    @Test
    void addCommentTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerAndItemAndStatusEqualsAndEndBefore(any(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(commentMapper.makeComment(any())).thenReturn(comment);
        when(commentMapper.makeCommentDto(any())).thenReturn(commentDto);

        CommentDto dtoReturned = itemService.addComment(commentDtoIn, 1, 1);
        assertEquals(commentDto.getText(), dtoReturned.getText());
    }

    @Test
    void CommentUserExceptionTest() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        ItemException e = assertThrows(ItemException.class, () -> itemService.addComment(commentDtoIn, 1, 1));

        assertEquals("Вещь с ИД 1 не найдена", e.getMessage());
    }

    @Test
    void CommentItemExceptionTest() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());


        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> itemService.addComment(commentDtoIn, 1, 1));

        assertEquals("Пользователь с ИД 1 не найден", e.getMessage());
    }

    @Test
    void CommentEmpty() throws Exception {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerAndItemAndStatusEqualsAndEndBefore(any(), any(), any(), any()))
                .thenReturn(List.of());

        BadRequestException e = assertThrows(BadRequestException.class, () -> itemService.addComment(commentDtoIn, 1, 1));

        assertEquals("Не возможно добавить комментарий", e.getMessage());

    }
}
