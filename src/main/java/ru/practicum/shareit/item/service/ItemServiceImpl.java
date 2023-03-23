package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User с ИД " + userId + " не найден"));
        List<Item> items = itemRepository.findAllByOwner(user).stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(toList());
        loadLastBooking(items);
        loadNextBooking(items);
        loadComments(items);
        log.info("Получены все вещи юзера с ИД {}", userId);
        return itemMapper.toListDto(items);
    }

    @Override
    public ItemDto getItem(Integer itemId, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User с ИД " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemException("Вещь с ИД " + itemId + " не найдена"));
        if (item.getOwner().getId().equals(user.getId())) {
            loadLastBooking(List.of(item));
            loadNextBooking(List.of(item));
        }
        loadComments(List.of(item));

        log.info("Получена вещь с ИД: {}", userId);
        return itemMapper.makeItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto create(Integer userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User с ИД " + userId + " не найден"));
        Item item = itemMapper.makeItem(itemDto);
        item.setOwner(user);
        log.info("Вещь пользователя {} добавлена", user);
        return itemMapper.makeItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Integer userId, ItemDto itemDto, Integer itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemException("Вещь с ИД " + itemId + " не найдена"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с ИД " + userId + " не найден"));

        if (!item.getOwner().equals(user)) {
            log.warn("Item with id: {} not belongs to user with id {}", itemId, userId);
            throw new ItemException(
                    String.format("Item with id: %s not belongs to user with id: %s", itemId, userId));
        }
        if (itemDto.getName() != null && !(itemDto.getName().isBlank())) {
            log.info("Item with id: {} updated name {}", itemId, itemDto.getName());
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !(itemDto.getDescription().isBlank())) {
            log.info("Item with id: {} updated description {}", itemId, itemDto.getDescription());
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            log.info("Item with id: {} updated available status {}", itemId, itemDto.getAvailable());
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Item with id: {}, owner with id: {} updated", itemId, userId);
        return itemMapper.makeItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> searchItem(String request) {
        if (request.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Поиск вещи: {}", request);
        List<Item> items = itemRepository.findItemByText(request);
        System.out.println(items);
        return itemRepository.findItemByText(request).stream()
                .map(itemMapper::makeItemDto).collect(toList());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDtoIn commentDtoIn, Integer userId, Integer itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemException("Вещь с ИД " + itemId + " не найдена"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь с ИД " + userId + " не найден"));
        if (bookingRepository.findAllByBookerAndItemAndStatusEqualsAndEndBefore(user, item, Status.APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("Не возможно добавить комментарий");
        }
        Comment comment = commentMapper.makeComment(commentDtoIn);
        comment.setAuthor(userRepository.findById(userId).orElse(null));
        comment.setItem(itemRepository.findById(itemId).orElse(null));
        CommentDto dto = commentMapper.makeCommentDto(commentRepository.save(comment));
        log.info("Comment added with text {}", dto.getText());
        return dto;
    }

    private void loadLastBooking(List<Item> items) {
        Map<Item, List<Booking>> bookingsLast =
                bookingRepository.findByItemInAndStatusEqualsAndStartLessThanEqualOrderByStartDesc(items, Status.APPROVED,
                                LocalDateTime.now())
                        .stream()
                        .collect(groupingBy(Booking::getItem, toList()));

        items.forEach(item -> item.setLastBooking(bookingsLast.getOrDefault(item, List.of())
                .stream().findFirst().orElse(null)));
    }

    private void loadNextBooking(List<Item> items) {
        Map<Item, List<Booking>> bookingsNext =
                bookingRepository.findByItemInAndStatusEqualsAndStartAfterOrderByStart(items, Status.APPROVED,
                                LocalDateTime.now())
                        .stream()
                        .collect(groupingBy(Booking::getItem, toList()));

        items.forEach(item -> item.setNextBooking(bookingsNext.getOrDefault(item, List.of())
                .stream().findFirst().orElse(null)));
    }

    private void loadComments(List<Item> items) {
        Map<Item, Set<Comment>> comments = commentRepository.findByItemIn(items)
                .stream()
                .collect(groupingBy(Comment::getItem, toSet()));

        items.forEach(item -> item.setComments(comments.getOrDefault(item, Collections.emptySet())));
    }
}
