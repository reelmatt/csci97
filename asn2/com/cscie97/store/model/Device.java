package com.cscie97.store.model;

/**
 *
 *
 *
 * @author Matthew Thomas
 */
public class Device {
    /** */
    private String id;

    /** */
    private String name;

    /** */
    private String location;

    /**
     * Device Constructor
     *
     *
     *
     * @param id
     * @param name
     * @param location
     */
    public Device(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public void respondToEvent(String event) {
        System.out.println("Responding to event: " + event);
        return;
    }

    /** Returns the Device id. */
    public String getId() {
        return this.id;
    }

    /** Returns the Device name. */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the locationId where the Device is.
     *
     * <store_id>:<aisle_id>
     */
    public String getLocation() {
        return this.location;
    }

    public String getStore() {
        String[] ids = this.location.split(":");
        return ids[0];
    }

    /**
     * Override default toString method.
     *
     * Displays details of the device, including the id, name, and location.
     */
    public String toString() {
        String device;

        device = String.format("Device '%s' -- %s\n", this.id, this.name);
        device += String.format("\tLocated @ %s", this.location);

        return device;
    }
}