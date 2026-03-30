package com.hotel;

import com.hotel.model.*;
import com.hotel.service.*;

import java.time.LocalDate;

public class DataSeeder {
    public static void main(String[] args) {
        RoomService rs = new RoomService();
        CustomerService cs = new CustomerService();
        BookingService bs = new BookingService(rs, cs);

        // Add extra rooms
        rs.addRoom(new Room(103, RoomType.STANDARD));
        rs.addRoom(new Room(203, RoomType.DELUXE));
        rs.addRoom(new Room(302, RoomType.SUITE));

        // Add Customers
        cs.addCustomer(new Customer("C001", "Alice Smith", "555-0101"));
        cs.addCustomer(new Customer("C002", "Bob Johnson", "555-0102"));
        cs.addCustomer(new Customer("C003", "Charlie Brown", "555-0103"));
        cs.addCustomer(new Customer("C004", "Diana Prince", "555-0104"));
        cs.addCustomer(new Customer("C005", "Evan Wright", "555-0105"));

        // Add Bookings
        try {
            bs.bookRoom("C001", 101, LocalDate.now().minusDays(1), LocalDate.now().plusDays(2));
            bs.bookRoom("C002", 201, LocalDate.now(), LocalDate.now().plusDays(3));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            bs.saveAll("hotel_data.dat");
            System.out.println("hotel_data.dat successfully seeded with static data!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
