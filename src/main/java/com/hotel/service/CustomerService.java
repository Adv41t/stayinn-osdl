package com.hotel.service;

import com.hotel.model.Customer;

import java.util.ArrayList;
import java.util.Iterator;

public class CustomerService {

    private ArrayList<Customer> customers = new ArrayList<>();

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public ArrayList<Customer> getAllCustomers() {
        return customers;
    }

    public Customer findByID(String customerID) {
        for (Customer c : customers) {
            if (c.getCustomerID().equalsIgnoreCase(customerID)) return c;
        }
        return null;
    }

    public boolean removeCustomer(String customerID) {
        Iterator<Customer> iterator = customers.iterator();
        while (iterator.hasNext()) {
            Customer c = iterator.next();
            if (c.getCustomerID().equalsIgnoreCase(customerID)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean customerIDExists(String customerID) {
        return findByID(customerID) != null;
    }
}
