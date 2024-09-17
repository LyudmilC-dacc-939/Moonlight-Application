package com.moonlight.asset;

import com.moonlight.model.bar.Bar;
import com.moonlight.model.bar.Screen;
import com.moonlight.repository.bar.BarRepository;
import com.moonlight.repository.bar.ScreenRepository;
import com.moonlight.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Order(7)
public class BarScreensAsset implements CommandLineRunner {

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private BarRepository barRepository;

    @Autowired
    private CsvService csvService;


    @Override
    public void run(String... args) throws Exception {
        List<String> screensFromCsv = csvService.readBarScreensFromCsv("assetDocs/barScreens.csv");

        Bar bar = barRepository.findByBarName("Moonlight Bar")
                .orElseGet(this::createNewBar);

        saveScreens(screensFromCsv, bar);
    }

    private Bar createNewBar() {
        Bar newBar = new Bar();
        newBar.setBarName("Moonlight Bar");
        return barRepository.save(newBar);
    }

    private void saveScreens(List<String> screensFromCsv, Bar bar) {
        for (String screenName : screensFromCsv) {

            if (screenRepository.findByScreenNameAndBarId(screenName, bar.getId()).isEmpty()) {
                Screen screen = new Screen();
                screen.setScreenName(screenName);
                screen.setBar(bar);

                screenRepository.save(screen);
            }
        }
    }
}

