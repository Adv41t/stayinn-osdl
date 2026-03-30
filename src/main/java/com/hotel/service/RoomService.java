package com.hotel.service;

import com.hotel.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoomService {

    private ArrayList<Room> rooms = new ArrayList<>();

    public RoomService() {
        // Seed some default rooms
        rooms.add(new Room(101, RoomType.STANDARD));
        rooms.add(new Room(102, RoomType.STANDARD));
        rooms.add(new Room(201, RoomType.DELUXE));
        rooms.add(new Room(202, RoomType.DELUXE));
        rooms.add(new Room(301, RoomType.SUITE));
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public ArrayList<Room> getAllRooms() {
        return rooms;
    }

    // Uses Iterator explicitly as required
    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        Iterator<Room> iterator = rooms.iterator();
        while (iterator.hasNext()) {
            Room r = iterator.next();
            if (r.isAvailable()) {
                available.add(r);
            }
        }
        return available;
    }

    public Room findRoomByNumber(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) return r;
        }
        return null;
    }

    public boolean roomNumberExists(int roomNumber) {
        return findRoomByNumber(roomNumber) != null;
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(rooms);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            rooms = (ArrayList<Room>) ois.readObject();
        }
    }
}
