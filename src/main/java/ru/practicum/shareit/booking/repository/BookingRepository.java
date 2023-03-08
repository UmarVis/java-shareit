package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Integer userId);

    @Query(value = "select * " +
            "from bookings b " +
            "where booker_id = ?1 " +
            "and item_id = ?2 " +
            "limit 1 ",
            nativeQuery = true)
    Optional<Booking> findItemIdByBooker(Integer bookerId, Integer itemId);

    @Query("select b " +
            "from Booking b " +
            "left join Item i on b.itemId = i.id " +
            "where i.owner = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerStartDesc(Integer userId);

    @Query(value = "select * " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.id = ?1 " +
            "and b.end_time < ?2 " +
            "order by b.end_time desc " +
            "limit 1",
            nativeQuery = true)
    Optional<Booking> findLastBooking(Integer itemId, LocalDateTime dateTime);

    @Query(value = "select * " +
            "from bookings b " +
            "left join items i on b.item_id = i.id " +
            "where i.id = ?1 " +
            "and b.start_time > ?2 " +
            "order by b.start_time " +
            "limit 1",
            nativeQuery = true)
    Optional<Booking> findNextBooking(Integer itemId, LocalDateTime dateTime);
}
