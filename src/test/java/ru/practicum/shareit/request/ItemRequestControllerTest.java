package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    RequestService requestService;

    LocalDateTime now = LocalDateTime.now();
    private final ItemDtoIn itemDtoIn = ItemDtoIn.builder().id(1).name("itemTest").description("itemDesc").available(true)
            .requestId(1).build();
    private final ItemRequestDtoOut dtoOut = new ItemRequestDtoOut(1, "descTest", now, Set.of(itemDtoIn));
    private final ItemRequestDtoIn dtoIn = new ItemRequestDtoIn("test");

    @Test
    void createTest() throws Exception {
        when(requestService.create(anyInt(), any()))
                .thenReturn(dtoOut);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(dtoIn))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dtoOut.getId()))
                .andExpect(jsonPath("$.description").value(dtoOut.getDescription()))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isNotEmpty());

        verify(requestService).create(anyInt(), any());
    }

    @Test
    void getRequestByUserTest() throws Exception {
        when(requestService.getRequestByUser(anyInt()))
                .thenReturn(List.of(dtoOut));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(dtoOut.getDescription())))
                .andExpect(jsonPath("$[0].items.size()", is(dtoOut.getItems().size())));
    }

    @Test
    void getByIdTest() throws Exception {
        when(requestService.getById(anyInt(), anyInt()))
                .thenReturn(dtoOut);

        mockMvc.perform(get("/requests/{requestId}", dtoOut.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(dtoOut.getDescription())))
                .andExpect(jsonPath("$.items.size()", is(dtoOut.getItems().size())));
    }

    @Test
    void getAllTest() throws Exception {
        when(requestService.getAll(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(dtoOut));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(dtoOut.getDescription())))
                .andExpect(jsonPath("$[0].items.size()", is(dtoOut.getItems().size())));
    }

    @Test
    void createTestValid() throws Exception {
        ItemRequestDtoIn dtoInValid = new ItemRequestDtoIn(" ");
        when(requestService.create(anyInt(), any()))
                .thenReturn(dtoOut);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(dtoInValid))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllValidTest() throws Exception {
        when(requestService.getAll(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(dtoOut));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest());
    }
}
