package com.cscie97.store.controller;

import com.cscie97.store.model.Observer;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.model.StoreModelService;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.StoreModelServiceInterface;

/**
 * MissingPersonCommand.
 *
 * @author Matthew Thomas
 */
public class StoreControllerService implements StoreControllerServiceInterface, Observer {
    private StoreModelServiceInterface storeModel;
    private Ledger ledger;

    public StoreControllerService() {
        this.storeModel = new StoreModelService();
        try {
            this.ledger = new Ledger("a", "b", "c");
        } catch (LedgerException e) {
            System.err.println(e);
        }
    }

    public void update(String event) {
        return;
    }

    public Command createCommand(String event) {
        Device device = new Device("a", "b", "c");
        Command test = new CustomerSeenCommand(device);
        return test;
    }

    public void execute() {
        return;
    };

    public StoreModelServiceInterface getStoreModel() {
        return this.storeModel;
    }
}