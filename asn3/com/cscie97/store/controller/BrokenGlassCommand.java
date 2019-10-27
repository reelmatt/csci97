package com.cscie97.store.controller;

import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;

/**
 * BrokenGlassCommand.
 *
 * A BrokenGlassCommand is created when a Microphone detects the sound of
 * glass breaking.
 *
 * @author Matthew Thomas
 */
public class BrokenGlassCommand extends AbstractCommand {
    /** The Aisle the broken glass is located in. */
    private Aisle aisle;

    /** Command message format to send to robot. */
    private static final String MESSAGE = "Clean up broken glass in %s.";

    /**
     * BrokenGlassCommand Constructor.
     *
     * @param   authToken   Token to authenticate with StoreModel API
     * @param   storeModel  StoreModel to get/update state.
     * @param   source      The Device which detected the event.
     * @param   aisle       The Aisle the broken glass is located in.
     */
    public BrokenGlassCommand(String authToken,
                              StoreModelServiceInterface storeModel,
                              Device source,
                              Aisle aisle) {
        super(authToken, storeModel, source);
        this.aisle = aisle;
    }

    /**
     * {@inheritDoc}
     *
     * A Robot is commanded to the Aisle where the glass broke.
     */
    public void execute() {
        try {
            // Get a robot
            Appliance robot = super.getOneAppliance(ApplianceType.ROBOT);

            // Send it a command to clean up
            String message = String.format(MESSAGE, this.aisle.getId());
            super.sendCommand(robot, message);
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}