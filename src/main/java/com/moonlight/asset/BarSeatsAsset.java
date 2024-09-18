package com.moonlight.asset;

import com.moonlight.model.bar.Screen;
import com.moonlight.model.bar.Seat;
import com.moonlight.repository.bar.ScreenRepository;
import com.moonlight.repository.bar.SeatRepository;
import com.moonlight.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(8)
public class BarSeatsAsset implements CommandLineRunner {

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private CsvService csvService;

    @Override
    public void run(String... args) throws Exception {
        List<String[]> seatsFromCsv = csvService.readBarSeatsFromCsv("assetDocs/barSeats.csv");

        saveSeats(seatsFromCsv);
    }

    private void saveSeats(List<String[]> seatsFromCsv) throws Exception {
        for (String[] seatData : seatsFromCsv) {
            String screenName = seatData[0].trim();
            Screen screen = screenRepository.findByScreenName(screenName)
                    .orElseThrow(() -> new Exception("Screen not found: " + screenName));

            for (int i = 1; i < seatData.length; i++) {
                int seatNumber = Integer.parseInt(seatData[i].trim());

                if (seatRepository.findBySeatNumberAndScreen(seatNumber, screen).isEmpty()) {
                    Seat newSeat = new Seat();
                    newSeat.setSeatNumber(seatNumber);
                    newSeat.setScreen(screen);

                    seatRepository.save(newSeat);
                }
            }
        }
    }
}

