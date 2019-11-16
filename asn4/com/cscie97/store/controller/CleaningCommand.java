package com.cscie97.store.controller;

import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.authentication.AuthToken;
import com.cscie97.store.authentication.AuthenticationException;
import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.InvalidAuthTokenException;

/**
 * CleaningCommand.
 *
 * A CleaningCommand is created when a Camera detects Product on the floor of
 * an Aisle.
 *
 * @author Matthew Thomas
 */
public class CleaningCommand extends AbstractCommand {
    /** The Product to be cleaned. */
    private Product product;

    /** The Aisle the mess is located in. */
    private Aisle aisle;

    /** Command message format to send to robot. */
    private static final String CLEANING_MESSAGE = "Clean up %s in %s.";

    /**
     * CleaningCommand Constructor.
     *
     * @param   authToken   Token to authenticate with StoreModel API
     * @param   storeModel  StoreModel to get/update state.
     * @param   source      The Device which detected the event.
     * @param   product     The Product to be cleaned.
     * @param   aisle       The Aisle the mess is located in.
     */
    public CleaningCommand(AuthToken authToken,
                           StoreModelServiceInterface storeModel,
                           Device source,
                           Product product,
                           Aisle aisle) {
        super(authToken, storeModel, source);
        this.product = product;
        this.aisle = aisle;
    }

    /**
     * {@inheritDoc}
     *
     * The first available Robot is commanded to clean up the Product.
     */
    public void execute() {
        try {
            // Get a robot
            Appliance robot = super.getOneAppliance(ApplianceType.ROBOT);

            // Send it a command to clean up
            String message = String.format(CLEANING_MESSAGE, this.product.getName(), this.aisle.getId());
            super.sendCommand(robot, message);
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}