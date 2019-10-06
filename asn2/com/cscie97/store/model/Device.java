package com.cscie97.store.model;

public class Device {

    private String id;
    private String name;
    private String location;

    public Device(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public void respondToEvent(String event) {
        System.out.println("Responding to event: " + event);
        return;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public String getStore() {
        String[] ids = this.location.split(":");
        return ids[0];
    }

    public String toString() {
        return "Device #" + this.id + " -- " + this.name;
    }
}