package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;

    private final ItemDtoIn dtoIn = new ItemDtoIn(1, "name", "desc", true, 1);
    private final ItemDtoOut dtoOut = new ItemDtoOut(1, "name", "desc", true, null,
            null, null, 1, 2);
    private final CommentDtoIn commentDtoIn = new CommentDtoIn("text");
    private final CommentDto commentDto = new CommentDto(1, "text", "author", LocalDateTime.now());

    @Test
    void createTest() throws Exception {
        when(itemService.create(anyInt(), any()))
                .thenReturn(dtoOut);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(dtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dtoOut.getId()))
                .andExpect(jsonPath("$.name").value(dtoOut.getName()))
                .andExpect(jsonPath("$.description").value(dtoOut.getDescription()))
                .andExpect(jsonPath("$.owner").value(1))
                .andExpect(jsonPath("$.requestId").value(2));

        verify(itemService).create(anyInt(), any());
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.getItem(anyInt(), anyInt()))
                .thenReturn(dtoOut);

        mockMvc.perform(get("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(dtoOut.getName())))
                .andExpect(jsonPath("$.description", is(dtoOut.getDescription())));

        verify(itemService).getItem(anyInt(), anyInt());
    }

    @Test
    void getUserItemsTest() throws Exception {
        when(itemService.getUserItems(anyInt()))
                .thenReturn(List.of(dtoOut));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(dtoOut.getName())))
                .andExpect(jsonPath("$[0].description", is(dtoOut.getDescription())));

        verify(itemService).getUserItems(anyInt());
    }

    @Test
    void updateTest() throws Exception {
        dtoOut.setName("randomItemUpdated");
        when(itemService.update(anyInt(), any(), anyInt()))
                .thenReturn(dtoOut);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(dtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(dtoOut.getName())))
                .andExpect(jsonPath("$.description", is(dtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(dtoOut.getAvailable())));
    }

    @Test
    void searchItemTest() throws Exception {
        when(itemService.searchItem(anyString()))
                .thenReturn(List.of(dtoOut));

        mockMvc.perform(get("/items/search")
                        .param("text", "аккумуляторная"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(dtoOut.getName())))
                .andExpect(jsonPath("$[0].description", is(dtoOut.getDescription())))
                .andExpect(jsonPath("$[0].available", is(dtoOut.getAvailable())));
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.addComment(any(), anyInt(), anyInt()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(notNullValue())));
    }
}
