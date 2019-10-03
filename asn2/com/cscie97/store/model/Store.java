package com.cscie97.store.model;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Store {
    private String id;
    private String name;
    private String address;
    private Map<Integer, Aisle> aisleMap;
    private Map<String, Device> deviceMap;
    private List<Customer> customerList;

    public Store (String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.aisleMap = new HashMap<Integer, Aisle>();
        this.deviceMap = new HashMap<String, Device>();
        this.customerList = new ArrayList<Customer>();
    }

    public void updateCustomer() {

    }

    public void updateInventory() {

    }

    public void addAisle(Aisle aisle) {
        this.aisleMap.put(aisle.getNumber(), aisle);
    }

    public void addDevice(Device device) {
        this.deviceMap.put(device.getId(), device);
    }

    public Aisle getAisle(Integer number) {
        return this.aisleMap.get(number);
    }

    public Device getDevice(String id) {
        return this.deviceMap.get(id);
    }

    public Map<Integer, Aisle> getAisleList() {
        return this.aisleMap;
    }

    public String printAisles() {
        Iterator<Map.Entry<Integer, Aisle>> aisles = this.aisleMap.entrySet().iterator();

        String output = "";


        while( aisles.hasNext() ) {
            Aisle aisle = aisles.next().getValue();
            output += aisle + "|\n";
        }
        return output;

    }

    public String printDevices() {
        Iterator<Map.Entry<String, Device>> devices = this.deviceMap.entrySet().iterator();

        String output = "";

        while (devices.hasNext() ) {
            Device device = devices.next().getValue();
            output += device;
        }

        return output;
    }

    public String getId() {
        return this.id;
    }
    public String toString() {
        String separator = "-----------------------\n";
        String store = separator;
        store += String.format("| Store %s -- %s\n", this.id, this.name);
        store += String.format("| Address: %s\n", this.address);
        store += separator;

        store += " Customers:\n";
        store += this.customerList;
        store += "\n" + separator;

        store += String.format("| Aisles:\n|\n%s", printAisles());
//        store += this.aisleMap;
        store += "\n" + separator;

        store += " Inventory:\n";
        store += "\n" + separator;

        store += String.format("| Devices:\n|\n%s", printDevices());
//        store += " Devices:\n";
//        store += this.deviceMap;
        store += "\n" + separator;

        return store;
//        return "Store #" + this.id + " -- " + this.name;
    }
}