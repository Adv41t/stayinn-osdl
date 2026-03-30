package com.hotel.model;

import java.io.Serializable;

public class DeluxeRoom extends AbstractRoom implements Serializable {
    private static final long serialVersionUID = 4L;

    public DeluxeRoom(int roomNumber) {
        super(roomNumber, RoomType.DELUXE, RoomType.DELUXE.getPricePerNight());
    }

    @Override
    public double calculateTariff(int nights) {
        // Deluxe: 10% discount for stays longer than 3 nights
        double total = pricePerNight * nights;
        if (nights > 3) total *= 0.90;
        return total;
    }
}
