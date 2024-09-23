package com.moonlight.service.impl.restaurant;

import com.moonlight.advice.exception.InvalidDateRangeException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.restaurant.RestaurantReservationRequest;
import com.moonlight.dto.restaurant.TableAvailabilityResponse;
import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;
import com.moonlight.repository.restaurant.RestaurantRepository;
import com.moonlight.repository.restaurant.RestaurantReservationRepository;
import com.moonlight.service.RestaurantReservationService;
import com.moonlight.service.impl.user.CurrentUserImpl;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Data
public class RestaurantReservationServiceImpl implements RestaurantReservationService {
    @Autowired
    private RestaurantReservationRepository restaurantReservationRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CurrentUserImpl currentUserImpl;


    @Override
    @SneakyThrows
    @Transactional
    public RestaurantReservation createReservation(RestaurantReservationRequest request, User user) {
        if (!currentUserImpl.isCurrentUserMatch(user)) {
            throw new RecordNotFoundException("This user is not authorize to proceed this operation");
        } else {
            Restaurant restaurantTable = restaurantRepository.findByTableNumber(request.getTableNumber())
                    .orElseThrow(() -> new RecordNotFoundException("Table not found"));

            if (request.getNumberOfPeople() > restaurantTable.getMaxNumberOfSeats()) {
                throw new UnavailableResourceException("This table is not suitable for the number of selected people.");
            }

            if (request.isSmoking() != restaurantTable.getRestaurantZone().isSmokerArea()) {
                throw new UnavailableResourceException("This table isn't in smoking area");
            }

            double totalCost = restaurantTable.getRestaurantZone().getSeatPrice() * request.getNumberOfPeople();

            LocalDateTime reservationStartTime = LocalDateTime.of(request.getReservationDate(), request.getReservationTime());
            LocalDateTime reservationEndTime = reservationStartTime.plusHours(1);
            if (reservationStartTime.isBefore(LocalDateTime.now())) {
                throw new InvalidDateRangeException("Reservation date and time can't be in the past");
            }
            boolean reservationExists = restaurantReservationRepository.alreadyExistingReservation(
                    restaurantTable.getId(),
                    request.getReservationDate(),
                    reservationStartTime,
                    reservationEndTime,
                    request.getTableNumber()) == 1;
            if (reservationExists) {
                throw new InvalidDateRangeException("Reservation already exists for this table, try for different hour");
            }
            RestaurantReservation reservation = new RestaurantReservation();
            reservation.setUser(user);
            reservation.setRestaurant(restaurantTable);
            reservation.setReservationDate(request.getReservationDate());
            reservation.setReservationTime(reservationStartTime);
            reservation.setReservationEndTime(reservationEndTime);
            reservation.setZone(restaurantTable.getRestaurantZone());
            reservation.setSeatCost(totalCost);
            reservation.setTableNumber(request.getTableNumber());
            reservation.setSmoking(request.isSmoking());

            return restaurantReservationRepository.save(reservation);

        }
    }

    @Override
    @Transactional
    @SneakyThrows
    public TableAvailabilityResponse getAvailableTablesByDateAndPreferences
            (LocalDate reservationDate,
             LocalTime startTime, LocalTime endTime, Integer seats, Boolean isSmoking) {
        if (reservationDate == null || reservationDate.equals(LocalDate.now())) {
            reservationDate = LocalDate.now();
        }
        final LocalTime startOfDay;
        final LocalTime endOfDay = (endTime != null) ? endTime : LocalTime.of(23, 59);

        if (reservationDate.equals(LocalDate.now())) {
            startOfDay = (startTime != null) ? startTime : LocalTime.now();
        } else {
            startOfDay = (startTime != null) ? startTime : LocalTime.MIDNIGHT;
        }

        List<Object[]> reservedTableData = restaurantReservationRepository
                .findAvailableTablesByDateAndPreferences(reservationDate, seats, isSmoking);

        Map<Long, List<String[]>> sortedMap = new TreeMap<>();

        List<Restaurant> allTables = restaurantRepository.findAll();

        allTables.stream()
                .filter(table -> seats == null || table.getMaxNumberOfSeats() >= seats)
                .filter(table -> isSmoking == null
                        || (isSmoking && table.getRestaurantZone().isSmokerArea()
                        || (!isSmoking && !table.getRestaurantZone().isSmokerArea())))
                .forEach(table -> {
                    List<String[]> availableSlots = new ArrayList<>();
                    availableSlots.add(new String[]{startOfDay.toString(), endOfDay.toString()});

                    LocalTime lastReservedEnd = null;

                    for (Object[] data : reservedTableData) {
                        Long tableNumber = (Long) data[0];
                        LocalTime reservedStart = ((Timestamp) data[1]).toLocalDateTime().toLocalTime();
                        LocalTime reservedEnd = ((Timestamp) data[2]).toLocalDateTime().toLocalTime();

                        if (!tableNumber.equals(table.getTableNumber())) {
                            continue;
                        }
                        List<String[]> newAvailableSlots = new ArrayList<>();

                        for (String[] slot : availableSlots) {
                            LocalTime slotStart = LocalTime.parse(slot[0]);
                            LocalTime slotEnd = LocalTime.parse(slot[1]);

                            if (slotStart.isBefore(reservedEnd) && slotEnd.isAfter(reservedStart)) {
                                if (reservedStart.isAfter(slotStart)) {
                                    newAvailableSlots.add(new String[]
                                            {slotStart.toString(), reservedStart.toString()});
                                }
                                if (lastReservedEnd != null && lastReservedEnd.equals(reservedStart)) {
                                } else {
                                    if (reservedEnd.isBefore(slotEnd)) {
                                        newAvailableSlots.add(new String[]
                                                {reservedEnd.toString(), slotEnd.toString()});
                                    }
                                }
                                lastReservedEnd = reservedEnd;
                            } else {
                                newAvailableSlots.add(slot);
                            }
                        }
                        availableSlots = newAvailableSlots;
                    }
                    if (!availableSlots.isEmpty()) {
                        String[] lastSlot = availableSlots.get(availableSlots.size() - 1);
                        LocalTime lastEndTime = LocalTime.parse(lastSlot[1]);
                        if (lastEndTime.isBefore(endOfDay)) {
                            availableSlots.add(new String[]{lastEndTime.toString(), endOfDay.toString()});
                        }
                    }
                    sortedMap.put(table.getTableNumber(), availableSlots);
                });
        Map<String, List<String[]>> availabilityMap = new LinkedHashMap<>();
        sortedMap.forEach((tableNumber, slot) -> {
            String tableKey = "Table number: " + tableNumber + ", zone: "
                    + restaurantRepository.getReferenceById(tableNumber).getRestaurantZone();
            availabilityMap.put(tableKey, (List<String[]>) slot);
        });
        return new TableAvailabilityResponse(reservationDate, availabilityMap);
    }

    public List<RestaurantReservation> getRestaurantReservationsByUserId(Long userId) {
        return restaurantReservationRepository.findByUserIdOrderByReservationDateReservationDateAsc(userId);
    }
}