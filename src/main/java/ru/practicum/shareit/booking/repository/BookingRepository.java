package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBooker(User user, Pageable pageable);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime time, LocalDateTime timeNow, Pageable pageable);

    @Query("select b from Booking b where b.booker.id = ?1 and b.status = ?2")
    List<Booking> findAllByBookerIdAndStatusEquals(Integer booker, Status status, Pageable pageable);

    List<Booking> findAllByItemOwner(User owner, Pageable pageable);

    List<Booking> findAllByItemOwnerAndEndBefore(User owner, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartAfter(User owner, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime time, LocalDateTime timeNow, Pageable pageable);

    @Query("select b from Booking b where b.item.owner = ?1 and b.status = ?2")
    List<Booking> findAllByItemOwnerAndStatusEquals(User owner, Status status, Pageable pageable);

    List<Booking> findByItemInAndStatusEqualsAndStartLessThanEqualOrderByStartDesc(List<Item> item, Status status, LocalDateTime time);

    List<Booking> findByItemInAndStatusEqualsAndStartAfterOrderByStart(List<Item> item, Status status, LocalDateTime time);

    List<Booking> findAllByBookerAndItemAndStatusEqualsAndEndBefore(User booker, Item item, Status status,
                                                                    LocalDateTime time);
}

