package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, Item> itemMap = new HashMap<>();
    private Integer itemId = 1;

    @Override
    public List<Item> getUserItems(Integer userId) {

        return itemMap.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Integer id) {
        if (id == null) {
            log.error("Item ID not found {} ", id);
            throw new ItemException(String.format("Item with id: %s not found", id));
        }
        log.info("Item with ID {} was found", id);
        return itemMap.get(id);
    }

    @Override
    public Item create(Item item) {
        item.setId(itemId++);
        itemMap.put(item.getId(), item);

        return item;
    }

    @Override
    public List<Item> searchItem(String word) {
        return itemMap.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(word.toLowerCase())
                        || item.getDescription().toLowerCase().contains(word.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Item deleteItem(Integer id) {
        return itemMap.remove(id);
    }
}
