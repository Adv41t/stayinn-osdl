package com.hotel.model;

public enum RoomType {
    STANDARD(1000.0),
    DELUXE(2000.0),
    SUITE(4000.0);

    private final double basePricePerNight;

    RoomType(double basePricePerNight) {
        this.basePricePerNight = basePricePerNight;
    }

    public double getPricePerNight() {
        return basePricePerNight;
    }
}
