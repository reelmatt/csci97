package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.StoreModelServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 * EmergencyCommand.
 *
 * @author Matthew Thomas
 */
public class EmergencyCommand implements Command {
    /** Emergency types */
    private enum Emergency {FIRE, FLOOD, EARTHQUAKE, ARMED_INTRUDER};

    private Device source;

    private Emergency emergency;

    private StoreModelServiceInterface storeModel;

    public EmergencyCommand(Device source, StoreModelServiceInterface storeModel) {
        this.source = source;
        this.emergency = Emergency.FIRE;
        this.storeModel = storeModel;
    }

    public void execute() {
        System.out.println("\nEXECUTING EmergencyCommand.");

        try {

//            turnstiles.forEach(turnstile -> turnstile.processCommand("open turnstile"));
            // Open turnstiles
            for (Appliance turnstile : this.storeModel.getAppliances("authToken", ApplianceType.TURNSTILE, "store_123")) {
                this.storeModel.receiveCommand("authToken", turnstile.getId(), "open");
            }

            // Announce emergency
            for (Appliance speaker : this.storeModel.getAppliances("authToken", ApplianceType.SPEAKER, "store_123")) {
                this.storeModel.receiveCommand("authToken", speaker.getId(), "Announce emergency.");
            }

            // Send robot to address
            List<Appliance> robots = this.storeModel.getAppliances("authToken", ApplianceType.ROBOT, "store_123");

            Appliance helperRobot = robots.remove(0);
            this.storeModel.receiveCommand("authToken", helperRobot.getId(), "Address emergency...");

            // Send remaining robots to assist customers
            for (Appliance robot : robots) {
                this.storeModel.receiveCommand("authToken", robot.getId(), "Assist customers leaving the store.");
            }


        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}