package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwner(Integer id);

    @Query("SELECT i FROM Item i WHERE (lower(i.name) LIKE lower(CONCAT('%',:word,'%') ) OR " +
            "lower(i.description) LIKE lower(CONCAT('%',:word,'%') ) AND i.available = true )")
    List<Item> findItemByText(@Param("word") String word);

}
