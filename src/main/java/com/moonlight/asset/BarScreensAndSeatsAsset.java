package com.moonlight.asset;

import com.moonlight.model.bar.Bar;
import com.moonlight.model.bar.Seat;
import com.moonlight.model.enums.Screen;
import com.moonlight.repository.bar.BarRepository;
import com.moonlight.repository.bar.SeatRepository;
import com.moonlight.service.CsvService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
@Order(7)
public class BarScreensAndSeatsAsset implements CommandLineRunner {


    @Autowired
    private BarRepository barRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private CsvService csvService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<String[]> dataFromCsv = csvService.readBarScreensAndSeatsFromCsv("assetDocs/barScreensAndSeats.csv");

        Bar bar = barRepository.findByBarName("Moonlight Bar")
                .orElseGet(this::createNewBar);

        saveBarAndSeats(dataFromCsv, bar);
    }

    private Bar createNewBar() {
        Bar newBar = new Bar();
        newBar.setBarName("Moonlight Bar");
        newBar.setScreens(new HashSet<>());
        return barRepository.save(newBar);
    }

    private void saveBarAndSeats(List<String[]> dataFromCsv, Bar bar) {
        for (String[] rowData : dataFromCsv) {
            String barName = rowData[0].trim();
            String screenName = rowData[1].trim();
            Screen screen = Arrays.stream(Screen.values())
                    .filter(s -> s.getDefaultScreenName().equals(screenName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown screen name: " + screenName));

            bar = barRepository.findByBarName(barName)
                    .orElseGet(() -> {
                        Bar newBar = new Bar();
                        newBar.setBarName(barName);
                        newBar.setScreens(new HashSet<>());
                        return barRepository.save(newBar);
                    });

            if (bar.getScreens().stream().noneMatch(s -> s.equals(screen))) {
                bar.getScreens().add(screen);
            }

            for (int i = 2; i < rowData.length; i++) {
                int seatNumber = Integer.parseInt(rowData[i].trim());

                boolean seatExists = seatRepository.existsByScreenAndSeatNumber(screen, seatNumber);
                if (!seatExists) {
                    Seat seat = new Seat();
                    seat.setScreen(screen);
                    seat.setSeatNumber(seatNumber);
                    seat.setBar(bar);

                    seatRepository.save(seat);
                }
            }
        }

        barRepository.save(bar);
    }
}

