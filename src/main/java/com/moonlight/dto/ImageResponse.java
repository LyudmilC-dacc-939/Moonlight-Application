package com.moonlight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ImageResponse {

    private Long imageId;
    private Long carId;
    private String imageData;
}
