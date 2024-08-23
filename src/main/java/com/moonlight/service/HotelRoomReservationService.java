package com.moonlight.service;

import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.model.hotel.HotelRoomReservation;

import java.time.LocalDate;

public interface HotelRoomReservationService {

    boolean datesOverlap (LocalDate existingStart, LocalDate existingEnd, LocalDate newStart, LocalDate newEnd);

    boolean CheckRoomAvailability (HotelRoom room, LocalDate startDate, LocalDate endDate);

    HotelRoomReservation makeReservation(
            Long userId, Long  roomNumber, LocalDate startDate, LocalDate endDate
            , int guestsAdult, int guestChildren);

    int duration(LocalDate startDate, LocalDate endDate);

    double totalCost (int duration, HotelRoom hotelRoom);
}