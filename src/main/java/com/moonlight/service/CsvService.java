package com.moonlight.service;

import java.io.IOException;
import java.util.List;


public interface CsvService {
    List<String> readAmenityFromCsv(String filePath) throws IOException;

    List<String[]> readRoomsFromCsv(String filePath) throws IOException;

    List<String[]> readCarsFromCsv(String filePath) throws IOException;

    List<String[]> readRestaurantTablesFromCsv(String filePath) throws IOException;

    List<String[]> readBarScreensAndSeatsFromCsv(String filePath) throws IOException;
}
