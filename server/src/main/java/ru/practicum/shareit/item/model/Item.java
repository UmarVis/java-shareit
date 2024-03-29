package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Set;


@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "available")
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Transient
    private Booking lastBooking;
    @Transient
    private Booking nextBooking;
    @Transient
    private Set<Comment> comments;
    @Column(name = "request_id")
    private Integer requester;
}
