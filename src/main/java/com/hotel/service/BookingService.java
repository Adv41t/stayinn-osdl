package com.hotel.service;

import com.hotel.model.*;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BookingService {

    private ArrayList<Booking> bookings = new ArrayList<>();

    // HashMap: roomNumber -> guestName (required by spec)
    private HashMap<Integer, String> roomGuestMap = new HashMap<>();

    private RoomService roomService;
    private CustomerService customerService;

    public BookingService(RoomService roomService, CustomerService customerService) {
        this.roomService = roomService;
        this.customerService = customerService;
    }

    public String bookRoom(String customerID, int roomNumber,
                           LocalDate checkIn, LocalDate checkOut) throws Exception {

        Customer customer = customerService.findByID(customerID);
        if (customer == null) throw new Exception("Customer not found: " + customerID);

        Room room = roomService.findRoomByNumber(roomNumber);
        if (room == null) throw new Exception("Room not found: " + roomNumber);

        if (!room.isAvailable()) {
            throw new Exception("Room " + roomNumber + " is already occupied!");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new Exception("Check-out date must be after check-in date.");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalCost = room.getPricePerNight() * nights;

        String bookingID = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = new Booking(bookingID, customerID, customer.getName(),
                roomNumber, room.getRoomType(), checkIn, checkOut, totalCost);

        bookings.add(booking);
        room.setAvailable(false);
        customer.setAllocatedRoomNumber(roomNumber);
        roomGuestMap.put(roomNumber, customer.getName());

        return bookingID;
    }

    public void checkout(int roomNumber) throws Exception {
        Room room = roomService.findRoomByNumber(roomNumber);
        if (room == null) throw new Exception("Room not found: " + roomNumber);
        if (room.isAvailable()) throw new Exception("Room " + roomNumber + " is not currently booked.");

        room.setAvailable(true);
        roomGuestMap.remove(roomNumber);

        // Clear the customer's allocated room
        for (var customer : customerService.getAllCustomers()) {
            if (customer.getAllocatedRoomNumber() == roomNumber) {
                customer.setAllocatedRoomNumber(-1);
                break;
            }
        }

        // Mark booking as ended (we keep it in history)
    }

    public ArrayList<Booking> getAllBookings() {
        return bookings;
    }

    public HashMap<Integer, String> getRoomGuestMap() {
        return roomGuestMap;
    }

    public Booking findActiveBookingByRoom(int roomNumber) {
        for (int i = bookings.size() - 1; i >= 0; i--) {
            Booking b = bookings.get(i);
            if (b.getRoomNumber() == roomNumber) return b;
        }
        return null;
    }
    public void saveAll(String filename) throws IOException {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
        oos.writeObject(roomService.getAllRooms());
        oos.writeObject(customerService.getAllCustomers());
        oos.writeObject(bookings);
        oos.writeObject(roomGuestMap);
    }
}

@SuppressWarnings("unchecked")
public void loadAll(String filename) throws IOException, ClassNotFoundException {
    File f = new File(filename);
    if (!f.exists()) return;

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
        ArrayList<Room> rooms        = (ArrayList<Room>)          ois.readObject();
        ArrayList<Customer> custs    = (ArrayList<Customer>)      ois.readObject();
        ArrayList<Booking> bkgs      = (ArrayList<Booking>)       ois.readObject();
        HashMap<Integer,String> map  = (HashMap<Integer,String>)  ois.readObject();

        roomService.getAllRooms().clear();
        roomService.getAllRooms().addAll(rooms);

        customerService.getAllCustomers().clear();
        customerService.getAllCustomers().addAll(custs);

        bookings.clear();
        bookings.addAll(bkgs);

        roomGuestMap.clear();
        roomGuestMap.putAll(map);
    }
}
}
