package com.moonlight.dto;

import com.moonlight.model.enums.RoomBedType;
import com.moonlight.model.enums.RoomType;
import com.moonlight.model.enums.RoomView;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelRoomRequest {
    @Positive(message = "Room number must be a positive number")
    @NotNull(message = "Room should have room number")
    private Long roomNumber;

    @NotNull(message = "Room type is mandatory")
    private RoomType roomType;

    @NotNull(message = "Room view is mandatory")
    private RoomView roomView;

    @NotNull(message = "Bed type is mandatory")
    private RoomBedType bedType;
}
