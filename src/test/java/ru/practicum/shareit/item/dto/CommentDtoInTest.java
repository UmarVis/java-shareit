package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.CommentDtoIn;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoInTest {
    @Autowired
    JacksonTester<CommentDtoIn> jacksonTester;

    @Test
    void CommentDtoInTest() throws Exception {
        CommentDtoIn commentDtoIn = new CommentDtoIn("text");

        JsonContent<CommentDtoIn> result = jacksonTester.write(commentDtoIn);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }
}
