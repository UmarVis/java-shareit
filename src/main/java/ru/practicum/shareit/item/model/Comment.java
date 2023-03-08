package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "text")
    private String text;
    @Column(name = "author_id")
    private Integer authorId;
    @Column(name = "item_id")
    private Integer itemId;
    @Column(name = "created")
    private LocalDateTime created;
}
