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
        Customer customer = null;
        StoreModelServiceException error = null;
        // try with ID
        try {
            customer = super.getStoreModel().getCustomer(super.getAuthToken(), this.customerName.toLowerCase());

        } catch (StoreModelServiceException e) {
            // handle error below
            error = e;
        }

        // try with Name
        try {
            customer = super.getStoreModel().getCustomerByName(super.getAuthToken(), super.getSource().getStore(), this.customerName.toLowerCase());

        } catch (StoreModelServiceException e) {
            // handle error below
            error = e;
        }

        // report back to Customer the location
        try {
            String message;
            if (customer == null) {
                message = error.getReason();
            } else {
                message = String.format(MESSAGE, this.customerName, customer.customerLocation());
            }

            Appliance helperSpeaker = super.getOneAppliance(ApplianceType.SPEAKER);
            super.sendCommand(helperSpeaker, message);

        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };

}