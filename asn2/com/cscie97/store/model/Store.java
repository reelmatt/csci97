package com.cscie97.store.model;

import java.util.Map;
import java.util.HashMap;

public class Store {
    private String id;
    private String name;
    private String address;
    private Map<Integer, Aisle> locationMap;

    public Store (String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.locationMap = new HashMap<Integer, Aisle>();
    }

    public void updateCustomer() {

    }

    public void updateInventory() {

    }

    public void addAisle(Aisle aisle) {
        this.locationMap.put(aisle.getNumber(), aisle);
        System.out.println("added aisle " + aisle.getNumber() + " to store " + this.id);
    }

    public Aisle getAisle(String id) {
        return this.locationMap.get(Integer.parseInt(id));
    }

    public Map<Integer, Aisle> getAisleList() {
        return this.locationMap;
    }

    public String getId() {
        return this.id;
    }
    public String toString() {
        return "Store #" + this.id + " -- " + this.name;
    }
}