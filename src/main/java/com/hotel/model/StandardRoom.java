package com.hotel.model;

import java.io.Serializable;

public class StandardRoom extends AbstractRoom implements Serializable {
    private static final long serialVersionUID = 3L;

    public StandardRoom(int roomNumber) {
        super(roomNumber, RoomType.STANDARD, RoomType.STANDARD.getPricePerNight());
    }

    @Override
    public double calculateTariff(int nights) {
        // Standard: flat rate
        return pricePerNight * nights;
    }
}
