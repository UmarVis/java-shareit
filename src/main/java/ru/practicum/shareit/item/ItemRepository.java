package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getUserItems(Integer userId);

    Item getById(Integer id);

    Item create(Item item);

    List<Item> searchItem(String word);

    Item deleteItem(Integer id);
}
