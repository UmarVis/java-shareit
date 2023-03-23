package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBooker(User user, Sort sort);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime time, Sort sort);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime time, LocalDateTime timeNow, Sort sort);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2")
    List<Booking> findAllByBookerIdAndStatusEquals(Integer booker, Status status, Sort sort);

    List<Booking> findAllByItemOwner(User owner, Sort sort);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime time, Sort sort);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime time, LocalDateTime timeNow, Sort sort);

    @Query("select b from Booking b where b.item.owner = ?1 and b.status = ?2")
    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, Status status, Sort sort);

    List<Booking> findByItemInAndStatusEqualsAndStartLessThanEqualOrderByStartDesc
            (List<Item> item, Status status, LocalDateTime time);

    List<Booking> findByItemInAndStatusEqualsAndStartAfterOrderByStart
            (List<Item> item, Status status, LocalDateTime time);

    List<Booking> findAllByBookerAndItemAndStatusEqualsAndEndBefore(User booker, Item item, Status status,
                                                                    LocalDateTime time);
}

