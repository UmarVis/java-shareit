package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Integer, Item> itemMap = new HashMap<>();
    private Integer itemId = 1;

    public List<Item> getUserItems(Integer userId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    public Item getById(Integer id) {
        Optional<Item> item = Optional.ofNullable(itemMap.get(id));
        if (item.isPresent()) {
            log.info("Item with ID {} was found", id);
            return item.get();
        } else {
            log.error("Item ID not found {} ", id);
            throw new ItemException(String.format("Item with id: %s not found", id));
        }
    }

    public Item create(Item item) {
        if (itemMap.containsKey(item.getId())) {
            throw new ItemException(String.format("Item with id %s already exist", item.getId()));
        }
        if (item.getId() == null) {
            item.setId(itemId++);
        }
        itemMap.put(item.getId(), item);
        return item;
    }

    public List<Item> searchItem(String word) {
        return itemMap.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(word.toLowerCase())
                        || item.getDescription().toLowerCase().contains(word.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Item deleteItem(Integer id) {
        return itemMap.remove(id);
    }

}
