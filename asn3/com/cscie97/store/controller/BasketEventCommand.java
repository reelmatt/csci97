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
 * BasketEventCommand.
 *
 * @author Matthew Thomas
 */
public class BasketEventCommand extends AbstractCommand {


    public BasketEventCommand(String authToken, StoreModelServiceInterface storeModel, Device source) {
        super(authToken, storeModel, source);
    }

    public void execute() {
        return;
    };
}