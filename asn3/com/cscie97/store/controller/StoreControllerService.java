package com.cscie97.store.controller;

import com.cscie97.store.model.Observer;
import com.cscie97.ledger.Ledger;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.Device;

/**
 * StoreControllerService.
 *
 * @author Matthew Thomas
 */
public class StoreControllerService implements StoreControllerServiceInterface, Observer {
    private StoreModelServiceInterface storeModel;
    private Ledger ledger;
    private String authToken;
    private CommandFactory factory;

    public StoreControllerService(StoreModelServiceInterface storeModel, Ledger ledger) {
        // Init
        this.storeModel = storeModel;
        this.ledger = ledger;
        this.authToken = "authToken implemented in assignment 4";

        // Initialize a CommandFactory to create commands to execute
        this.factory = new CommandFactory(storeModel, ledger);

        // Register with Store Model as an Observer
        this.storeModel.register(this);
    }

    public void update(Device device, String event) {
        Command storeCommand = null;
        try {
            storeCommand = factory.createCommand(getAuthToken(), event, device);
        } catch (StoreControllerServiceException e) {
            System.err.println(e);
        }

        try {
            if (storeCommand != null) {
                storeCommand.execute();
            }
        } catch (StoreControllerServiceException e) {
            System.err.println(e);
        }
        return;
    }

    public StoreModelServiceInterface getStoreModel() {
        return this.storeModel;
    }

    public Ledger getLedger() {
        return this.ledger;
    }

    public String getAuthToken() {
        return this.authToken;
    }
}