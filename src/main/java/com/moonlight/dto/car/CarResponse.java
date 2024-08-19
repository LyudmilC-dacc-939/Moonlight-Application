package com.moonlight.dto.car;

import com.moonlight.model.car.FileResource;
import com.moonlight.model.enums.CarType;
import lombok.Data;

import java.util.List;

@Data
public class CarResponse {

    private CarType type;
    private String carBrand;
    private List<FileResource> fileResources;

}
