package com.cscie97.store.model;

public class Device {

    private String id;
    private String name;
    private Aisle location;

    public Device(String id, String name, Aisle location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public void respondToEvent() {
        System.out.println("Responding....");
        return;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Aisle getLocation() {
        return this.location;
    }

    public String toString() {
        return "Device #" + this.id + " -- " + this.name;
    }
}