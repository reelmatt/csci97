package com.cscie97.store.model;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Store {
    /** Store ID */
    private String id;

    /** Name of the Store */
    private String name;

    /** Physical address of the Store. */
    private String address;

    /** A Map of Aisles in the Store. */
    private List<Aisle> aisleList;

    /** A List of Customers currently in the Store. */
    private List<Customer> customerList;

    /**
     * Store Constructor
     *
     *
     *
     * @param id
     * @param name
     * @param address
     */
    public Store (String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.aisleList = new ArrayList<Aisle>();
        this.customerList = new ArrayList<Customer>();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Aisle getAisle(String aisleId) {
        for (Aisle aisle : getAisleList()) {
            if (aisleId.equals(aisle.getId())) {
                return aisle;
            }
        }

        return null;
    }

    public List<Aisle> getAisleList() {
        return this.aisleList;
    }

    public void addAisle(Aisle aisle) {
        this.aisleList.add(aisle);
    }

    /**
     * Override default toString method.
     *
     * Displays details of the store including the id, name, address, active
     * customers, aisles, inventory, sensors, and appliances.
     */
    public String toString() {
        String separator = "-----------------------\n";
        String store = separator;

        // Store Info
        store += String.format("| Store %s -- %s\n", this.id, this.name);
        store += String.format("| Address: %s\n", this.address);
        store += separator;

        // List of customers
        store += " Customers:\n";
//        store += this.customerList;
        store += "\n" + separator;

        // List of aisles (with shelves/inventory)
        store += String.format("| Aisles:\n|\n");
//        for(Aisle aisle : getAisleList().values()) {
//            store += aisle + "\n";
//        }
        store += "\n" + separator;

        // Inventory List
        store += " Inventory:\n";
        store += "\n" + separator;

        // List of Devices in the Store
        store += String.format("| Devices:\n|\n");
//        for(Device device : getDeviceList().values()) {
//            store += device;
//        }
        store += "\n" + separator;

        return store;
    }
}