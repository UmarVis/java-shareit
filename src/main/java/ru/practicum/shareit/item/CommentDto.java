package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @Positive
    private Integer id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
