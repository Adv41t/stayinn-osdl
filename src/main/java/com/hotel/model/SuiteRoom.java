package com.hotel.model;

import java.io.Serializable;

public class SuiteRoom extends AbstractRoom implements Serializable {
    private static final long serialVersionUID = 5L;

    public SuiteRoom(int roomNumber) {
        super(roomNumber, RoomType.SUITE, RoomType.SUITE.getPricePerNight());
    }

    @Override
    public double calculateTariff(int nights) {
        // Suite: includes complimentary breakfast surcharge after first night
        double base = pricePerNight * nights;
        double breakfastSurcharge = (nights > 1) ? (nights - 1) * 30.0 : 0;
        return base + breakfastSurcharge;
    }
}
