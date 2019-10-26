package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.StoreModelServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 * BrokenGlassCommand.
 *
 * @author Matthew Thomas
 */
public class BrokenGlassCommand extends AbstractCommand {
    private Aisle aisle;

    public BrokenGlassCommand(String authToken, StoreModelServiceInterface storeModel, Device source, Aisle aisle) {
        super(authToken, storeModel, source);
        this.aisle = aisle;
    }

    public void execute() {
        try {
            // Get list of all robots
//            List<Appliance> robots = super.getAppliances(ApplianceType.ROBOT);
//
//            // Get a robot
//            Appliance robot = robots.remove(0);
            Appliance robot = super.getOneAppliance(ApplianceType.ROBOT);

            // Send it a command to clean up
            String message = String.format("Clean up broken glass in %s.", this.aisle.getId());
            super.sendCommand(robot, message);
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}