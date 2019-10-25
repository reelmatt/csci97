package com.cscie97.store.controller;

import com.cscie97.store.model.Device;

/**
 * EmergencyCommand.
 *
 * @author Matthew Thomas
 */
public class EmergencyCommand implements Command {
    /** Emergency types */
    private enum Emergency {FIRE, FLOOD, EARTHQUAKE, ARMED_INTRUDER};

    public Device source;

    public EmergencyCommand(Device source) {
        this.source = source;
    }

    public void execute() {
        System.out.println("\nEXECUTING EmergencyCommand.");

        System.out.println("Open all turnstiles.");
        System.out.println("Announce emergency.");
        System.out.println("Send Robot 1 to address.");
        System.out.println("Send remaining robots to assist.");


        return;
    };
}