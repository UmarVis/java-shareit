package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ItemException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        userService.getById(userId);
        List<Item> items = itemRepository.findAllByOwner(userId).stream()
                .sorted(Comparator.comparing(Item::getId))
                .collect(Collectors.toList());
        List<ItemDto> itemDto = new ArrayList<>();
        for (Item item : items) {
            List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId());
            ItemDto dto = itemMapper.makeItemDto(item);
            fillWithBookings(dto);
            fillWithCommentDtos(comments);
            itemDto.add(dto);
        }
        log.info("Получены все вещи юзера с ИД {}", userId);
        return itemDto;
    }

    @Override
    public ItemDto getItem(Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemException("Вещь с ИД " + itemId + " не найдена"));
        ItemDto dto = itemMapper.makeItemDto(item);
        if (item.getOwner().equals(userId)) {
            fillWithBookings(dto);
        }
        dto.setComments(fillWithCommentDtos(commentRepository.findAllByItemIdOrderByCreatedDesc(itemId)));
        log.info("Получена вещь с ИД: {}", userId);
        return dto;
    }

    @Override
    public ItemDto create(Integer userId, ItemDto itemDto) {
        userService.getById(userId);
        Item item = itemMapper.makeItem(itemDto);
        item.setOwner(userId);
        log.info("Вещь пользователя {} добавлена", userId);
        return itemMapper.makeItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Integer userId, ItemDto itemDto, Integer itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemException("Вещь с ИД " + itemId + " не найдена"));

        if (!item.getOwner().equals(userId)) {
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
                return itemRepository.findItemByText(request).stream()
                .map(itemMapper::makeItemDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        itemRepository.deleteById(id);
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Integer userId, Integer itemId) {
        UserDto userDto = userService.getById(userId);
        getItem(itemId, userId);
        Booking booking = bookingRepository.findItemIdByBooker(userId, itemId).orElseThrow(() ->
                new BadRequestException("User: " + userId + " not uses this item"));
        if (booking.getStatus().equals(Status.REJECTED)) {
            throw new BadRequestException("User: " + userId + " not uses this item");
        }
        if (!booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Comment only after booking end time expired");
        }
        Comment comment = commentMapper.makeComment(commentDto);
        comment.setAuthorId(userId);
        comment.setItemId(itemId);
        CommentDto dto = makeFullCommentDto(commentRepository.save(comment));
        dto.setAuthorName(userDto.getName());
        log.info("Comment added with text {}", dto.getText());
        return dto;
    }

    private ItemDto fillWithBookings(ItemDto itemDto) {
        Optional<Booking> lastBooking =
                Optional.ofNullable(bookingRepository.findLastBooking(itemDto.getId(), LocalDateTime.now()).stream()
                        .filter(booking -> !booking.getStatus().equals(Status.REJECTED))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now())).findFirst().orElse(null));
        itemDto.setLastBooking(lastBooking.orElse(null));

        Optional<Booking> nextBooking = Optional.ofNullable(bookingRepository.findNextBooking(itemDto.getId(),
                        LocalDateTime.now()).stream().filter(booking -> booking.getStatus().equals(Status.APPROVED))
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now())).findFirst().orElse(null));
        itemDto.setNextBooking(nextBooking.orElse(null));
        return itemDto;
    }

    private List<CommentDto> fillWithCommentDtos(List<Comment> comments) {
        List<CommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(makeFullCommentDto(comment));
        }
        return dtos;
    }

    private CommentDto makeFullCommentDto(Comment comment) {
        CommentDto dto = commentMapper.makeCommentDto(comment);
        dto.setAuthorName(userService.getById(comment.getAuthorId()).getName());
        return dto;
    }
}
