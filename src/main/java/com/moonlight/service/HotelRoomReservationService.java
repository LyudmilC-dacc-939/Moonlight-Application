package com.moonlight.service;

import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.model.hotel.HotelRoomReservation;

import java.time.LocalDate;
import java.util.List;

public interface HotelRoomReservationService {

    boolean datesOverlap(LocalDate existingStart, LocalDate existingEnd, LocalDate newStart, LocalDate newEnd);

    boolean CheckRoomAvailability(HotelRoom room, LocalDate startDate, LocalDate endDate);

    HotelRoomReservation createReservation(
            Long userId, Long roomNumber, LocalDate startDate, LocalDate endDate
            , int guestsAdult, int guestChildren);

    List<HotelRoomReservation> getRoomReservationsByUserId(Long userId);

}