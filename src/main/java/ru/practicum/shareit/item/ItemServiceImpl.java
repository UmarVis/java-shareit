package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
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
    private final ItemMapper mapper;

    @Override
    public List<ItemDto> getUserItems(Integer userId) {
        userRepository.getById(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemRepository.getUserItems(userId).stream()
                .forEach(item -> itemDtoList.add(mapper.makeItemDto(item)));
        log.info("User's with id: {} items: {}", userId, itemDtoList);
        return itemDtoList;
    }

    @Override
    public ItemDto getById(Integer itemId) {
        log.info("Getting item with id: {}", itemId);
        return mapper.makeItemDto(itemRepository.getById(itemId));
    }

    @Override
    public ItemDto create(Integer userId, ItemDto itemDto) {
        userRepository.getById(userId);
        Item item = mapper.makeItem(itemDto);
        item.setOwner(userId);
        log.info("User's {} item created", userId);
        return mapper.makeItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(Integer userId, ItemDto itemDto, Integer itemId) {
        Item item = itemRepository.getById(itemId);
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
        return mapper.makeItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(String request) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemRepository.searchItem(request).stream()
                .forEach(item -> itemDtoList.add(mapper.makeItemDto(item)));
        log.info("Searching item {}", request);
        return itemDtoList;
    }

    @Override
    public void delete(Integer id) {
        itemRepository.deleteItem(id);
    }

}
