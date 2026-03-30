package com.hotel.model;

import java.io.Serializable;

public class Room extends AbstractRoom implements Serializable {
    private static final long serialVersionUID = 2L;

    public Room(int roomNumber, RoomType roomType) {
        super(roomNumber, roomType, roomType.getPricePerNight());
    }

    public Room(int roomNumber, RoomType roomType, double pricePerNight) {
        super(roomNumber, roomType, pricePerNight);
    }

    @Override
    public double calculateTariff(int nights) {
        return pricePerNight * nights;
    }

    @Override
    public String toString() {
        return "Room #" + roomNumber + " [" + roomType + "] ₹" + pricePerNight + "/night - "
                + (isAvailable ? "Available" : "Occupied");
    }
}
