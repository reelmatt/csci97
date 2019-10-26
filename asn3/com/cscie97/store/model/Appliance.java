package com.cscie97.store.model;

/**
 * An Appliance is a Device that monitors and captures data about conditions in
 * the store and itself. The Appliance can be a speaker (to communicate with
 * customers), a robot (which communicates with customers as well as performs
 * tasks), or a turnstile (which communicates with customers as well as
 * opens/closes the turnstile).
 *
 * An Appliance differs from a Sensor, or general Device, in that it can respond
 * to commands and be controlled.
 */
public class Appliance extends Device {
    /** The type of Appliance. */
    private ApplianceType type;

    /**
     * Appliance Constructor.
     *
     * Creates an Appliance with the added property of a ApplianceType.
     *
     * @param id        Globally unique identifier.
     * @param name      Name of the Device.
     * @param location  Location of the Device (<store>:<aisle>).
     * @param type      The type of Appliance (speaker, robot, or turnstile).
     */
    public Appliance (String id, String name, String location, ApplianceType type) {
        super(id, name, location);
        this.type = type;
    }

    /**
     * Process a command from the Store Model Service.
     *
     * @param command   The command to process. (For assignment 2, this is an
     *                  opaque string. Further implementation will be completed
     *                  for assignment 3).
     */
    public void processCommand(String command) {
        System.out.println(
            String.format("Appliance '%s' received command '%s'.", super.getId(), command)
        );
        return;
    }

    public ApplianceType getType() {
        return type;
    }

    /**
     * Override default toString method.
     *
     * Displays details of the device, including the id, name, location, and type.
     */
    public String toString() {
        String appliance = super.toString();
        appliance += "\n\tType: " + this.type;

        return appliance;
    }
}