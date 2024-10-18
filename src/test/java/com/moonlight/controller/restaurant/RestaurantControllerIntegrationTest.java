package com.moonlight.controller.restaurant;

import com.moonlight.model.enums.RestaurantZone;
import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.repository.restaurant.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    //@WithMockUser(username = "lyudmil_filipov@abv.bg", roles = {"ADMIN_ROLE"})
    @Test
    public void testSearchRestaurantTable_Found_TwoParameters() throws Exception {
        Restaurant restaurant = new Restaurant(null, RestaurantZone.TERRACE, 1L, 4, new ArrayList<>());
        restaurantRepository.save(restaurant);

        mockMvc.perform(get("/api/v1/restaurants/search")
                        .param("tableNumber", "1")
                        .param("restaurantZone", RestaurantZone.TERRACE.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableNumber", is(1)))
                .andExpect(jsonPath("$[0].restaurantZone", is(RestaurantZone.TERRACE.toString())));
    }

    @Test
    public void testSearchRestaurantTable_Found_FirstParameter() throws Exception {
        Restaurant restaurant = new Restaurant(null, RestaurantZone.TERRACE, 1L, 4, new ArrayList<>());
        restaurantRepository.save(restaurant);

        mockMvc.perform(get("/api/v1/restaurants/search")
                        .param("tableNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tableNumber", is(1)));
    }

    @Test
    public void testSearchRestaurantTable_Found_SecondParameter() throws Exception {
        Restaurant restaurant = new Restaurant(null, RestaurantZone.TERRACE, 1L, 4, new ArrayList<>());
        restaurantRepository.save(restaurant);

        mockMvc.perform(get("/api/v1/restaurants/search")
                        .param("restaurantZone", "terrace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].restaurantZone", is(RestaurantZone.TERRACE.toString())));
    }

    @Test
    public void testSearchRestaurantTable_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/restaurants/search")
                        .param("tableNumber", "880")
                        .param("restaurantZone", "RANDOM"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No tables found with your search criteria.")));
    }

    @Test
    public void testSearchRestaurantTable_NoMatch() throws Exception {
        Restaurant restaurant = new Restaurant(null, RestaurantZone.TERRACE, 1L, 4, new ArrayList<>());
        restaurantRepository.save(restaurant);

        mockMvc.perform(get("/api/v1/restaurants/search")
                        .param("tableNumber", "50")
                        .param("restaurantZone", RestaurantZone.SALOON.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No tables found with your search criteria.")));
    }

    @Test
    public void testSearchRestaurantTable_Found_NoParameters() throws Exception {
        Restaurant restaurant = new Restaurant(null, RestaurantZone.TERRACE, 1L, 4, new ArrayList<>());
        restaurantRepository.save(restaurant);

        mockMvc.perform(get("/api/v1/restaurants/search"))
                .andExpect(status().isOk());
    }
}
