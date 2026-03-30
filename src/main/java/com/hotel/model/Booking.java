package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Booking implements Serializable {
    private static final long serialVersionUID = 7L;

    private String bookingID;
    private String customerID;
    private String customerName;
    private int roomNumber;
    private RoomType roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalCost;

    // Generic Pair associating roomNumber with guestName
    private Pair<Integer, String> roomGuestPair;

    public Booking(String bookingID, String customerID, String customerName,
                   int roomNumber, RoomType roomType,
                   LocalDate checkInDate, LocalDate checkOutDate, double totalCost) {
        this.bookingID = bookingID;
        this.customerID = customerID;
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalCost = totalCost;
        this.roomGuestPair = new Pair<>(roomNumber, customerName);
    }

    public String getBookingID() { return bookingID; }
    public String getCustomerID() { return customerID; }
    public String getCustomerName() { return customerName; }
    public int getRoomNumber() { return roomNumber; }
    public RoomType getRoomType() { return roomType; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public double getTotalCost() { return totalCost; }
    public Pair<Integer, String> getRoomGuestPair() { return roomGuestPair; }

    public long getNights() {
        return java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
}
