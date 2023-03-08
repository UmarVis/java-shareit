package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public CommentDto makeCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public Comment makeComment(CommentDto cDto) {
        return Comment.builder()
                .text(cDto.getText())
                .created(LocalDateTime.now())
                .build();
    }
}
