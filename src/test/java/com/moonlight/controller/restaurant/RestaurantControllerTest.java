package com.moonlight.controller.restaurant;


import com.moonlight.model.enums.RestaurantZone;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "server.port=8085")
public class RestaurantControllerTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8085;
    }

    @Test
    void testSearchRestaurantTable_Found() {
        given()
                .queryParam("tableNumber", 1)
                .queryParam("restaurantZone", "saloon")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/restaurants/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("$.size()", is(1))
                .body("[0].tableNumber", equalTo(1))
                .body("[0].restaurantZone", equalTo(RestaurantZone.SALOON.toString()))
                .body("[0]", hasKey("id"));
    }

    @Test
    void testSearchRestaurantTable_NotFound() {
        given()
                .queryParam("tableNumber", 999)
                .queryParam("restaurantZone", "RANDOM")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/restaurants/search")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("No tables found with your search criteria."));
    }

    @Test
    void testSearchRestaurantTable_Found_FirstParameter() {
        given()
                .queryParam("tableNumber", 1)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/restaurants/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("$.size()", is(1))
                .body("[0].tableNumber", equalTo(1))
                .body("[0].restaurantZone", equalTo(RestaurantZone.SALOON.toString()))
                .body("[0]", hasKey("id"));
    }

    @Test
    void testSearchRestaurantTable_Found_SecondParameter() {
        given()
                .queryParam("restaurantZone", "saloon")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/restaurants/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("$.size()", is(10))
                .body("[0].tableNumber", equalTo(1))
                .body("[0].restaurantZone", equalTo(RestaurantZone.SALOON.toString()))
                .body("[0]", hasKey("id"));
    }
}
