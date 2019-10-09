package com.cscie97.store.model;

/**
 * A Sensor is a Device that monitors and captures data about conditions in the
 * store. The Sensor can be a microphone (which listens for voice commands
 * from customers), or a camera (which updates the location of customers).
 */
public class Sensor extends Device {
    /** The type of Sensor. */
    private SensorType type;

    /**
     * Sensor Constructor.
     *
     * Creates a Device with the added property of a SensorType.
     *
     * @param id        Globally unique identifier.
     * @param name      Name of the Device.
     * @param location  Location of the Device (<store>:<aisle>).
     * @param type      The type of Sensor (microphone or camera).
     */
    public Sensor (String id, String name, String location, SensorType type) {
        super(id, name, location);
        this.type = type;
    }

    /**
     * Override default toString method.
     *
     * Displays details of the device, including the id, name, location, and type.
     */
    public String toString() {
        String sensor = super.toString();
        sensor += "\n\tType: " + this.type;

        return sensor;
    }
}