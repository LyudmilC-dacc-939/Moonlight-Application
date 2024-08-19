package com.moonlight.dto;

import com.moonlight.model.FileResource;
import com.moonlight.model.enums.CarType;
import lombok.Data;

import java.util.List;

@Data
public class CarResponse {

    private CarType type;
    private String carBrand;
    private List<FileResource> fileResources;

}
