package com.hotel.model;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 6L;

    private String customerID;
    private String name;
    private String contactNumber;
    private int allocatedRoomNumber; // -1 if none

    public Customer(String customerID, String name, String contactNumber) {
        this.customerID = customerID;
        this.name = name;
        this.contactNumber = contactNumber;
        this.allocatedRoomNumber = -1;
    }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public int getAllocatedRoomNumber() { return allocatedRoomNumber; }
    public void setAllocatedRoomNumber(int allocatedRoomNumber) { this.allocatedRoomNumber = allocatedRoomNumber; }

    public String getAllocatedRoomDisplay() {
        return allocatedRoomNumber == -1 ? "None" : String.valueOf(allocatedRoomNumber);
    }

    @Override
    public String toString() {
        return customerID + " - " + name;
    }
}
