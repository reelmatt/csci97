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
    private Map<Integer, Aisle> aisleMap;

    /** A Map of Devices in the Store. */
    private Map<String, Device> deviceMap;

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
        this.aisleMap = new HashMap<Integer, Aisle>();
        this.deviceMap = new HashMap<String, Device>();
        this.customerList = new ArrayList<Customer>();
    }

    public String getId() {
        return this.id;
    }

    public Map<Integer, Aisle> getAisleList() {
        return this.aisleMap;
    }

    public Aisle getAisle(Integer number) {
        return this.aisleMap.get(number);
    }

    public Device getDevice(String id) {
        return this.deviceMap.get(id);
    }

    public Map<String, Device> getDeviceList() {
        return this.deviceMap;
    }

    public void addAisle(Aisle aisle) {
        this.aisleMap.put(aisle.getNumber(), aisle);
    }

    public void addDevice(Device device) {
        this.deviceMap.put(device.getId(), device);
    }

    public String toString() {
        String separator = "-----------------------\n";
        String store = separator;

        // Store Info
        store += String.format("| Store %s -- %s\n", this.id, this.name);
        store += String.format("| Address: %s\n", this.address);
        store += separator;

        // List of customers
        store += " Customers:\n";
        store += this.customerList;
        store += "\n" + separator;

        // List of aisles (with shelves/inventory)
        store += String.format("| Aisles:\n|\n");
        for(Aisle aisle : getAisleList().values()) {
            store += aisle + "\n";
        }
        store += "\n" + separator;

        // Inventory List
        store += " Inventory:\n";
        store += "\n" + separator;

        // List of Devices in the Store
        store += String.format("| Devices:\n|\n");
        for(Device device : getDeviceList().values()) {
            store += device;
        }
        store += "\n" + separator;

        return store;
    }
}