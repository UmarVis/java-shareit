package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestMapper requestMapper;

    @InjectMocks
    private RequestServiceImpl requestService;

    LocalDateTime now = LocalDateTime.now();
    private final User user = new User(1, "user", "user@mail.ru");
    private final ItemRequest itemRequest = new ItemRequest(1, "desc", user, now, Set.of());
    private final ItemRequestDtoOut dtoOut = new ItemRequestDtoOut(1, "desc", now, null);
    private final ItemRequestDtoIn dtoIn = new ItemRequestDtoIn("desc");
    private final Item item = new Item(1, "name", "desc", true, user, null, null, null, 2);

    @Test
    void create_userNotFoundTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> requestService.create(user.getId(), dtoIn));
        assertEquals("Пользователь с ИД: 1 не найден", e.getMessage());

        verify(requestRepository, never()).save(any());
    }

    @Test
    void createTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(itemRequest);
        when(requestMapper.makeItemRequest(any())).thenReturn(itemRequest);
        when(requestMapper.makeItemRequestDtoOut(any())).thenReturn(dtoOut);

        ItemRequestDtoOut itemRequestDtoOut = requestService.create(user.getId(), dtoIn);

        assertEquals(dtoOut, itemRequestDtoOut);
        assertEquals(dtoOut.getDescription(), itemRequestDtoOut.getDescription());
        verify(requestRepository).save(any());
    }

    @Test
    void getRequestByUserTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterOrderByCreated(any())).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequesterIn(anyCollection())).thenReturn(Set.of(item));
        when(requestMapper.makeListItemRequestDtoWithItems(any())).thenReturn(List.of(dtoOut));

        List<ItemRequestDtoOut> actualRequests = requestService.getRequestByUser(user.getId());

        assertFalse(actualRequests.isEmpty());
        assertEquals(1, actualRequests.size());
        assertEquals(dtoOut.getId(), actualRequests.get(0).getId());
    }

    @Test
    void getByIdTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequesterIn(anyCollection())).thenReturn(Set.of(item));
        when(requestMapper.makeItemRequestDtoWithItems(itemRequest)).thenReturn(dtoOut);

        ItemRequestDtoOut dtoOutsReturned = requestService.getById(1, 1);

        assertEquals(dtoOut, dtoOutsReturned);
    }

    @Test
    void getByIdExceptionTest() throws Exception {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.empty());

        RequestNotFoundException e = assertThrows(RequestNotFoundException.class,
                () -> requestService.getById(1, 1));

        assertEquals("Запрос с ИД 1 не найден", e.getMessage());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getAllTest() throws Exception {
        Page<ItemRequest> requests = new PageImpl<>(List.of(itemRequest));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.findAll(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "created"))))
                .thenReturn(requests);
        when(itemRepository.findAllByRequesterIn(anyCollection())).thenReturn(Set.of(item));
        when(requestMapper.makeListItemRequestDtoWithItems(any())).thenReturn(List.of(dtoOut));

        List<ItemRequestDtoOut> dtoOutsReturned = requestService.getAll(user.getId(), 0, 20);

        assertThat(dtoOutsReturned.size()).isEqualTo(1);
        assertThat(dtoOutsReturned.get(0).getId()).isEqualTo(1L);
        assertThat(dtoOutsReturned.get(0).getDescription()).isEqualTo("desc");
        assertThat(dtoOutsReturned.get(0).getCreated()).isNotNull();
        assertThat(dtoOutsReturned.get(0).getItems()).isEqualTo(null);
    }
}
