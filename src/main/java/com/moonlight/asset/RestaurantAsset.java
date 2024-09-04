package com.moonlight.asset;

import com.moonlight.model.enums.RestaurantZone;
import com.moonlight.model.restaurant.Restaurant;
import com.moonlight.repository.restaurant.RestaurantRepository;
import com.moonlight.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(6)
public class RestaurantAsset implements CommandLineRunner {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CsvService csvService;

    @Override
    public void run(String... args) throws Exception {
        List<String[]> tablesFromCsv = csvService.readRestaurantTablesFromCsv("assetDocs/restaurantTables.csv");
        saveTables(tablesFromCsv);
    }

    private void saveTables(List<String[]> tablesFromCsv) throws Exception {
        for (String[] tableData : tablesFromCsv) {
            String restaurantZoneStr = tableData[0].trim().toUpperCase();
            RestaurantZone restaurantZone = RestaurantZone.valueOf(restaurantZoneStr);
            Long tableNumber = Long.parseLong(tableData[1]);
            int maxNumberOfSeats = Integer.parseInt(tableData[2]);

            Restaurant newTable = new Restaurant();
            newTable.setRestaurantZone(restaurantZone);
            newTable.setTableNumber(tableNumber);
            newTable.setMaxNumberOfSeats(maxNumberOfSeats);

            if (restaurantRepository.findByTableNumber(newTable.getTableNumber()).isEmpty()) {
                restaurantRepository.save(newTable);
            }
        }
    }
}
