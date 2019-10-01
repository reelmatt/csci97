package com.cscie97.store.model;

public class Store {
    private Integer id;
    private String name;
    private String address;

    public Store (Integer id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public void updateCustomer() {

    }

    public void updateInventory() {

    }

    public String toString() {
        return "Store #" + this.id + " -- " + this.name;
    }
}