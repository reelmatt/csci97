package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.StoreModelServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 * MissingPersonCommand.
 *
 * A MissingPersonCommand is created when a Customer asks a Microphone to locate
 * a Customer. When the query is asked, the Store Controller searches the Store
 * Model Service for the customer’s location and responds through the closest
 * Speaker in the Store to the Customer who asked.
 *
 * @author Matthew Thomas
 */
public class MissingPersonCommand extends AbstractCommand {
    /** Name of the Customer to located. */
    private String customerName;

    /** Command message format to send to robot. */
    private static final String MESSAGE = "Customer %s is located %s";

    /**
     * MissingPersonCommand Constructor.
     *
     * @param   authToken   Token to authenticate with StoreModel API
     * @param   storeModel  StoreModel to get/update state.
     * @param   source      The Device which detected the event.
     * @param   product     The Product to be cleaned.
     * @param   aisle       The Aisle the mess is located in.
     */
    public MissingPersonCommand(String authToken,
                                StoreModelServiceInterface storeModel,
                                Device source,
                                String customerName) {
        super(authToken, storeModel, source);
        this.customerName = customerName;
    }

    /**
     * {@inheritDoc}
     *
     *
     */
    public void execute() {
        // Convert name to lower case
        String name = this.customerName.toLowerCase();

        // Tracker variables
        Customer customer = null;
        StoreModelServiceException error = null;

        // Try name as customer ID
        try {
            customer = super.getStoreModel().getCustomer(super.getAuthToken(), name);
        } catch (StoreModelServiceException e) {
            // handle error below
            error = e;
        }

        // Try name as first name <space> last name
        try {
            customer = super.getStoreModel().getCustomerByName(super.getAuthToken(), super.getSource().getStore(), name);
        } catch (StoreModelServiceException e) {
            // handle error below
            error = e;
        }

        // report back to Customer the location, or error
        try {
            String message;

            // Customer wasn't found, include the Error in result message
            if (customer == null) {
                message = error.getReason();
            } else {
                message = String.format(MESSAGE, this.customerName, customer.customerLocation());
            }

            // Notification of Customer's location
            Appliance helperSpeaker = super.getOneAppliance(ApplianceType.SPEAKER);
            super.sendCommand(helperSpeaker, message);

        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };

}