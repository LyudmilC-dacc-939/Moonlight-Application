package com.moonlight.service.impl.hotel;


import com.moonlight.advice.exception.RoomNotAvailableException;
import com.moonlight.model.hotel.HotelRoom;
import com.moonlight.model.hotel.HotelRoomReservation;
import com.moonlight.model.user.User;
import com.moonlight.repository.hotel.HotelRoomRepository;
import com.moonlight.repository.hotel.HotelRoomReservationRepository;
import com.moonlight.repository.user.UserRepository;
import com.moonlight.service.HotelRoomReservationService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Data
public class HotelRoomReservationServiceImpl implements HotelRoomReservationService {
    @Autowired
    private HotelRoomRepository hotelRoomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelRoomReservationRepository hotelRoomReservationRepository;

    @Override
    public boolean datesOverlap(LocalDate existingStart, LocalDate existingEnd, LocalDate newStart, LocalDate newEnd) {
        if(existingStart.isEqual(existingEnd)&&newStart.isEqual(existingEnd )
                || newStart.isEqual(newEnd)&&existingEnd.isEqual(newStart)){
            return true;
        }
        return (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart));
    }

    @Override
    public boolean CheckRoomAvailability(HotelRoom room, LocalDate startDate, LocalDate endDate) {
        List<HotelRoomReservation> existingReservation = hotelRoomReservationRepository.findByHotelRoom(room);

        for (HotelRoomReservation reservation : existingReservation) {
            if (datesOverlap(reservation.getStartDate(), reservation.getEndDate(), startDate, endDate)) {
                return false; // room not available
            }
        }
        return true; // room available
    }

    @NotNull
    @Transactional
    @Override
    public HotelRoomReservation makeReservation(
            Long userId, Long roomNumber, LocalDate startDate, LocalDate endDate
            , int guestsAdult, int guestChildren) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        User user = userOptional.get();

        Optional<HotelRoom> hotelRoomOptional = hotelRoomRepository.findByRoomNumber(roomNumber);
        if (!hotelRoomOptional.isPresent()) {
            throw new RuntimeException("Hotel room not found with ID" + roomNumber);
        }
        HotelRoom hotelRoom = hotelRoomOptional.get();

        if (CheckRoomAvailability(hotelRoom, startDate, endDate)) {
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date must be after or equal to the start date!");
            }

            int totalGuests = guestsAdult+guestChildren;
            if(totalGuests > hotelRoom.getRoomType().getMaxNumberOfGuests()){
                throw new RoomNotAvailableException(
                        "Total number of guests exceeds maximum allowed guest for the room type.");
            }

            int duration =duration(startDate,endDate);
            double totalCost = totalCost(duration,hotelRoom);

            HotelRoomReservation reservation = new HotelRoomReservation();
            reservation.setUser(user);
            reservation.setHotelRoom(hotelRoom);
            reservation.setStartDate(startDate);
            reservation.setEndDate(endDate);
            reservation.setGuestsAdult(guestsAdult);
            reservation.setGuestsChildren(guestChildren);
            reservation.setDuration(duration);
            reservation.setTotalCost(totalCost);

            return hotelRoomReservationRepository.save(reservation);
        } else {
            throw new RoomNotAvailableException("The selected accommodation with number " +hotelRoom.getRoomNumber()+
                    ", type "+ hotelRoom.getRoomType()
                    + ", featuring a " + hotelRoom.getRoomView() + " view is unavailable for the selected period." +
                    "\nPlease review the available rooms or select different period and try again.");
        }
    }
    @Override
    public int duration(LocalDate startDate, LocalDate endDate) {
        int duration;
        if(startDate.isEqual(endDate)){
           duration = (int) (ChronoUnit.DAYS.between(startDate,endDate)+1);
        } else {
             duration = (int) (ChronoUnit.DAYS.between(startDate,endDate));
        }
        return duration;
    }
    @Override
    public double totalCost (int duration, HotelRoom hotelRoom) {
        return duration*hotelRoom.getRoomType().getRoomPricePerNight();
    }
}