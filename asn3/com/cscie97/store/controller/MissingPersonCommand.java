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

    public MissingPersonCommand(String authToken, StoreModelServiceInterface storeModel, Device source) {
        super(authToken, storeModel, source);
    }

    public void execute() {

        try {
            Customer customer = super.getStoreModel().getCustomer("authToken", "cust_2");


            // Send robot to address
//            List<Appliance> speakers = super.getAppliances(ApplianceType.SPEAKER);



//            Appliance helperSpeaker = speakers.remove(0);
            Appliance helperSpeaker = super.getOneAppliance(ApplianceType.SPEAKER);
            super.sendCommand(helperSpeaker, "Customer cust_2 is located:" + customer.customerLocation());
//            super.getStoreModel().receiveCommand(super.getAuthToken(), helperSpeaker.getId(), "Customer cust_2 is located:" + customer.customerLocation());

        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }


        return;
    };

}