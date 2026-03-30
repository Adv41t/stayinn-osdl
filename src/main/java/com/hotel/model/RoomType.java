package com.hotel.model;

public enum RoomType {
    STANDARD(100.0),
    DELUXE(200.0),
    SUITE(400.0);

    private final double basePricePerNight;

    RoomType(double basePricePerNight) {
        this.basePricePerNight = basePricePerNight;
    }

    public double getPricePerNight() {
        return basePricePerNight;
    }
}
