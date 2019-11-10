package com.cscie97.store.model;

/**
 * A Device that exists in the Store 24X7 System.
 *
 * A Device is globally unique within the System and can be classified as one
 * of two sub-types: Sensor or Appliance. A Device is responsible for capturing
 * data about conditions in the store; the kind and type of data can vary
 * depending on the type of Device. All Devices emit 'events' based on activity
 * within a store.
 *
 * @author Matthew Thomas
 */
public class Device {
    /** Device ID. */
    private String id;

    /** Name of the device. */
    private String name;

    /**
     * Location of the Device.
     * Stored as a fully-qualified aisle ID <store>:<aisle>
     */
    private String location;

    /**
     * Device Constructor
     *
     * Creates a Device with a globally unique identifier. Each Device has
     * a specific location at Aisle-level specificity.
     *
     * @param id        Globally unique identifier.
     * @param name      Name of the Device.
     * @param location  Location of the Device (<store>:<aisle>).
     */
    public Device(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    /**
     * Create an event to be processed by the Store Model Service.
     *
     * @param   event   An opaque string representing the event.
     * @return          The opaque event.
     */
    public String createEvent(String event) {
        return event;
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
     * Returns the location where the Device is, stored in the form
     *      <store_id>:<aisle_id>
     */
    public String getLocation() {
        return this.location;
    }

    /** Returns the store ID referenced in the Device location. */
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