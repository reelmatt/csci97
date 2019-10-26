package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.StoreModelServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 * CleaningCommand.
 *
 * @author Matthew Thomas
 */
public class CleaningCommand extends AbstractCommand {

    private Product product;

    private Aisle aisle;

    public CleaningCommand(String authToken,
                           StoreModelServiceInterface storeModel,
                           Device source,
                           Product product,
                           Aisle aisle) {
        super(authToken, storeModel, source);
        this.product = product;
        this.aisle = aisle;
    }

    public void execute() {
        try {
            // Get list of all robots
//            List<Appliance> robots = super.getAppliances(ApplianceType.ROBOT);

            // Get a robot
            Appliance robot = super.getOneAppliance(ApplianceType.ROBOT);
//            Appliance robot = robots.remove(0);

            // Send it a command to clean up
            String message = String.format("Clean up %s in %s.", this.product.getName(), this.aisle.getId());
            super.sendCommand(robot, message);
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}