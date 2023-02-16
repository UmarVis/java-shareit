package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<ItemDto> getUserItems(Integer userId) {
        try {
            userRepository.getById(userId).getId();
        } catch (NullPointerException e) {
            throw new UserNotFoundException(String.format("User %s not found", userId));
        }
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item items : itemRepository.getUserItems(userId)) {
            itemDtoList.add(makeItemDto(items));
        }
        log.info("User's with id: {} items: {}", userId, itemDtoList);
        return itemDtoList;
    }

    public ItemDto getById(Integer itemId) {
        log.info("Getting item with id: {}", itemId);
        return makeItemDto(itemRepository.getById(itemId));
    }

    public ItemDto create(Integer userId, ItemDto itemDto) {
        try {
            userRepository.getById(userId).getId();
        } catch (NullPointerException e) {
            throw new UserNotFoundException(String.format("User %s not found", userId));
        }
        Item item = makeItem(itemDto);
        item.setOwner(userId);
        log.info("User's {} item created", userId);
        return makeItemDto(itemRepository.create(item));
    }

    public ItemDto update(Integer userId, ItemDto itemDto, Integer itemId) {
        Item item = itemRepository.getById(itemId);
        if (item.getOwner() != userId) {
            log.warn("Item with id: {} not belongs to user with id {}", itemId, userId);
            throw new ItemException(
                    String.format("Item with id: %s not belongs to user with id: %s", itemId, userId));
        }
        itemRepository.deleteItem(itemId);
        if (itemDto.getName() != null) {
            log.info("Item with id: {} updated name {}", itemId, itemDto.getName());
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            log.info("Item with id: {} updated description {}", itemId, itemDto.getDescription());
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            log.info("Item with id: {} updated available status {}", itemId, itemDto.getAvailable());
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Item with id: {}, owner with id: {} updated", itemId, userId);
        return makeItemDto(itemRepository.create(item));
    }

    public List<ItemDto> searchItem(String request) {
        if (request.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item items : itemRepository.searchItem(request)) {
            itemDtoList.add(makeItemDto(items));
        }
        log.info("Searching item {}", request);
        return itemDtoList;
    }

    public void delete(Integer id) {
        itemRepository.deleteItem(id);
    }

    private ItemDto makeItemDto(Item item) {
        return ItemDto.builder().id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    private Item makeItem(ItemDto itemDto) {
        return Item.builder().id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
