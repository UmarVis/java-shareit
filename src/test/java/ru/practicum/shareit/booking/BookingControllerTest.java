package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;

    private final LocalDateTime now = LocalDateTime.now();
    private final BookingDtoIn bookingDtoIn = new BookingDtoIn(1, now.plusHours(1), now.plusHours(5));
    private final BookingDtoOut bookingDtoOut = new BookingDtoOut(1, now, now.plusHours(1), Status.APPROVED,
            new BookingDtoOut.ItemDto(1, "name"), new BookingDtoOut.UserDto(1, "name"));


    @Test
    void addBookingTest() throws Exception {
        when(bookingService.addBooking(anyInt(), any()))
                .thenReturn(bookingDtoOut);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoIn.getItemId()), Integer.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOut.getItem().getName())));

        verify(bookingService).addBooking(anyInt(), any());
    }

    @Test
    void approveTest() throws Exception {
        when(bookingService.approve(anyInt(), anyInt(), any()))
                .thenReturn(bookingDtoOut);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())))
                .andExpect(jsonPath("$.booker.name", is(bookingDtoOut.getItem().getName())));
    }

    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getById(any(), any()))
                .thenReturn(bookingDtoOut);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(bookingDtoOut.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoOut.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingDtoOut.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.item.name", is(bookingDtoOut.getItem().getName())));
    }

    @Test
    void getAllByUserTest() throws Exception {
        when(bookingService.getAllByUser(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoOut));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start", is(notNullValue())))
                .andExpect(jsonPath("$[0].end", is(notNullValue())))
                .andExpect(jsonPath("$[0].status", is(bookingDtoOut.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoOut.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoOut.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDtoOut.getItem().getName())));
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        when(bookingService.getAllByOwner(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoOut));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDtoOut.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start", is(notNullValue())))
                .andExpect(jsonPath("$[0].end", is(notNullValue())))
                .andExpect(jsonPath("$[0].status", is(bookingDtoOut.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtoOut.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtoOut.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDtoOut.getItem().getName())));

    }

    @Test
    void getAllByUserException() throws Exception {
        when(bookingService.getAllByUser(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoOut));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void addBookingTestException() throws Exception {
        BookingDtoIn bookingDtoInBad = new BookingDtoIn(1, now.minusHours(1), now.plusHours(5));
        when(bookingService.addBooking(anyInt(), any()))
                .thenReturn(bookingDtoOut);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDtoInBad))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
