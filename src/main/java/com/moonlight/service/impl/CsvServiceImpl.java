package com.moonlight.service.impl;

import com.moonlight.service.CsvService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvServiceImpl implements CsvService {

    public List<String> readAmenityFromCsv(String filePath) throws IOException {
        List<String> amenities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource(filePath).getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                amenities.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amenities;
    }

    public List<String[]> readRoomsFromCsv(String filePath) throws IOException {
        List<String[]> rooms = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource(filePath).getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] roomData = line.split(",");
                rooms.add(roomData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }
}
