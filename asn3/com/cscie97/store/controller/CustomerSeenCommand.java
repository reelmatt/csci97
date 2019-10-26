package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.StoreModelServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 * CustomerSeenCommand.
 *
 * @author Matthew Thomas
 */
public class CustomerSeenCommand extends AbstractCommand {
    private Aisle aisle;

    private Customer customer;

    public CustomerSeenCommand(String authToken, StoreModelServiceInterface storeModel, Device source, Customer customer, Aisle aisle) {
        super(authToken, storeModel, source);
        this.aisle = aisle;
        this.customer = customer;
    }

    public void execute() {
        try {
            String location = super.getSource().getStore() + ":" + this.aisle.getId();
            super.getStoreModel().updateCustomer(super.getAuthToken(), this.customer.getId(), location);
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}