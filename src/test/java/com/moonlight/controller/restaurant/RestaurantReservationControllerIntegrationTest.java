package com.moonlight.controller.restaurant;

import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.dto.restaurant.RestaurantReservationRequest;
import com.moonlight.dto.restaurant.RestaurantReservationResponse;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.enums.RestaurantZone;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;
import com.moonlight.service.RestaurantReservationService;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.condition.Not.not;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestaurantReservationService restaurantReservationService;

    private RestaurantReservationResponse mockResponse;

    @BeforeEach
    void setUp() {

        mockResponse = new RestaurantReservationResponse(
                1L,
                1L,
                LocalDate.now().plusDays(2),
                LocalTime.of(19, 0),
                RestaurantZone.SALOON,
                5L,
                false,
                20.0
        );
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void testCreateReservation_Success() throws Exception {
        RestaurantReservationRequest request = new RestaurantReservationRequest();
        request.setTableNumber(5L);
        request.setNumberOfPeople(4);
        request.setSmoking(false);
        request.setReservationDate(LocalDate.now().plusDays(1));
        request.setReservationTime(LocalTime.of(19, 0));


        User user = new User();
        user.setId(1L);
        user.setEmailAddress("testUser@gmail.com");

        RestaurantReservation reservation = new RestaurantReservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setTableNumber(5L);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setSeatCost(5.00);
        reservation.setSmoking(false);
        reservation.setReservationDate(LocalDate.now().plusDays(1));
        reservation.setReservationTime(LocalDateTime.of(reservation.getReservationDate(),LocalTime.of(17, 30)));

        when(restaurantReservationService.createReservation(any(RestaurantReservationRequest.class), any()))
                .thenReturn(reservation);

        mockMvc.perform(post("/api/v1/reservations/restaurant/create-reservation/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.id").value(reservation.getId()))
                .andExpect(jsonPath("$.tableNumber").value(reservation.getTableNumber()));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void testCreateReservation_UserMismatch() throws Exception {

        RestaurantReservationRequest request = new RestaurantReservationRequest();
        request.setTableNumber(5L);

        when(restaurantReservationService.createReservation(any(RestaurantReservationRequest.class), any()))
                .thenThrow(new RecordNotFoundException("This user is not authorized to proceed with this operation"));

        mockMvc.perform(post("/api/v1/reservations/restaurant/create-reservation/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationDate").value("Reservation date is required"))
                .andExpect(jsonPath("$.reservationTime").value("Reservation time is required"))
                .andExpect(jsonPath("$.numberOfPeople").value("Positive number is required"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void testCreateReservation_MissingNumberOfPeople() throws Exception {
        RestaurantReservationRequest request = new RestaurantReservationRequest();
        request.setTableNumber(5L);
        request.setSmoking(false);
        request.setReservationDate(LocalDate.now().plusDays(1));
        request.setReservationTime(LocalTime.of(19, 0));

        mockMvc.perform(post("/api/v1/reservations/restaurant/create-reservation/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.numberOfPeople").value("Positive number is required"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void testGetAvailableTables_MissingReservationDate() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/restaurant/available-tables")
                        .param("startTime", "19:00")
                        .param("endTime", "21:00")
                        .param("seats", "4")
                        .param("isSmoking", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void testGetAvailableTables_MissingSeats() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/restaurant/available-tables")
                        .param("reservationDate", LocalDate.now().plusDays(1).toString())
                        .param("startTime", "19:00")
                        .param("endTime", "21:00")
                        .param("isSmoking", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void testGetAvailableTables_MissingSmokingPreference() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/restaurant/available-tables")
                        .param("reservationDate", LocalDate.now().plusDays(1).toString())
                        .param("startTime", "19:00")
                        .param("endTime", "21:00")
                        .param("seats", "4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void testGetAvailableTables_NoTablesAvailable() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/restaurant/available-tables")
                        .param("reservationDate", LocalDate.now().plusDays(1).toString())
                        .param("startTime", "19:00")
                        .param("endTime", "21:00")
                        .param("seats", "10")
                        .param("isSmoking", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
