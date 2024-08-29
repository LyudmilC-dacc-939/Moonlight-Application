package com.moonlight.service.impl.car;

import com.moonlight.advice.exception.InvalidDateRangeException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.advice.exception.UnavailableResourceException;
import com.moonlight.dto.car.CarAvailabilityRequest;
import com.moonlight.dto.car.CarReservationRequest;
import com.moonlight.model.car.Car;
import com.moonlight.model.car.CarReservation;
import com.moonlight.model.enums.CarType;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.user.User;
import com.moonlight.repository.car.CarRepository;
import com.moonlight.repository.car.CarReservationRepository;
import com.moonlight.repository.user.UserRepository;
import com.moonlight.service.CarReservationService;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class CarReservationServiceImpl implements CarReservationService {

    @Autowired
    private CarReservationRepository carReservationRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @SneakyThrows
    @Transactional
    public CarReservation createReservation(CarReservationRequest request, String email) {

        // Checking Date Selection
        dateRangeValidator(request.getStartDate(), request.getEndDate());
        boolean isValidDateRange = request.getStartDate() != null &&
                request.getEndDate() != null &&
                !request.getEndDate().isBefore(request.getStartDate());
        if (!isValidDateRange) {
            throw new InvalidDateRangeException("End date must be after start date.");
        }
        LocalDate currentDate = LocalDate.now();
        if (request.getStartDate().isBefore(currentDate)) {
            throw new InvalidDateRangeException("Selected date must be in the Present or in the Future");
        }

        User user = userRepository.findByEmailAddress(email).orElseThrow(() ->
                new RecordNotFoundException(String.format("User with email %s does not exist", email)));

        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new RecordNotFoundException("Car not found"));

        // Check if the car is available during the requested period
        List<CarReservation> overlappingReservations = carReservationRepository.findOverlappingReservations(
                request.getCarId(), request.getStartDate(), request.getEndDate());

        if (!overlappingReservations.isEmpty()) {
            throw new UnavailableResourceException("Car is not available during the selected dates.");
        }

        CarType carType = car.getType();

        // Checks if selected car and amount of people can fit in the requested car.
        if (carType.getNumberOfSeats() < request.getNumberOfPeople()) {
            throw new UnavailableResourceException("This car is not suitable for the amount of selected people.");
        }

        long days = DAYS.between(request.getStartDate(), request.getEndDate());
        double totalCost = carType.getPrice() * days;

        if (totalCost == 0) {
            totalCost = carType.getPrice();
        }

        CarReservation reservation = new CarReservation();
        reservation.setUser(user);
        reservation.setCar(car);
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setTotalCost(totalCost);
        reservation.setStatus(ReservationStatus.PENDING); // Currently we have no logic for changing this TBD

        return carReservationRepository.save(reservation);
    }

    @Override
    public Map<LocalDate, List<String>> getAvailableCarsByDateRange(CarAvailabilityRequest request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        // Checking if the given date range is allowed
        dateRangeValidator(startDate, endDate);


        Map<LocalDate, List<String>> availabilityMap = new HashMap<>();

        // Loop through each day in the range
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Long> reservedCarIds = carReservationRepository.findReservedCarIdsByDateRange(date, date);

            List<String> availableCarModels = carRepository.findAll().stream()
                    .filter(car -> !reservedCarIds.contains(car.getId()))
                    .map(Car::getCarBrand)
                    .collect(Collectors.toList());

            availabilityMap.put(date, availableCarModels);
        }

        return availabilityMap;
    }

    @Override
    public List<CarReservation> getCarReservationsByUserId(Long userId) {
        return carReservationRepository.findByUserIdOrderByStartDate(userId);
    }

    private void dateRangeValidator(LocalDate startDate, LocalDate endDate) {
        boolean isValidDateRange = startDate != null &&
                endDate != null &&
                !endDate.isBefore(startDate);
        if (!isValidDateRange) {
            throw new InvalidDateRangeException("End date must be after start date.");
        }
        LocalDate currentDate = LocalDate.now();
        if (startDate.isBefore(currentDate)) {
            throw new InvalidDateRangeException("Selected date must be in the Present or in the Future");
        }
    }
}