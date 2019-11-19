package com.cscie97.store.controller;

import com.cscie97.ledger.Ledger;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Observer;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.authentication.AuthToken;
import com.cscie97.store.authentication.AuthenticationServiceInterface;

/**
 * StoreControllerService - concrete class
 *
 * {@inheritDoc}
 *
 * @author Matthew Thomas
 */
public class StoreControllerService implements StoreControllerServiceInterface, Observer {
    /** Authentication Service to use authenticate requests to public API. */
    private AuthenticationServiceInterface authService;

    /** The StoreModelService that provides API to update state. */
    private StoreModelServiceInterface storeModel;

    /** The Ledger which processes transactions. */
    private Ledger ledger;

    /** Token to authenticate actions. */
    private AuthToken adminToken = null;

    /** Factory to generate Command objects to execute. */
    private CommandFactory factory;

    /**
     * StoreControllerService Constructor
     *
     * Creates an instance of the Store Controller Service to respond to Store
     * events by updating state and processing transactions through the Ledger.
     *
     * @param storeModel    The StoreModelService that provides API to update state.
     * @param ledger        The Ledger which processes transactions.
     */
    public StoreControllerService(AuthenticationServiceInterface authService, StoreModelServiceInterface storeModel, Ledger ledger) {
        this.authService = authService;
        this.storeModel = storeModel;
        this.ledger = ledger;

        // Initialize a CommandFactory to create commands to execute
        this.factory = new CommandFactory(storeModel, authService, ledger);

        // Register with Store Model as an Observer
        this.storeModel.register(this);
    }

    /**
     * {@inheritDoc}
     *
     * In this implementation, Command objects are immediately executed after
     * creation. Other implmentations may support adding Commands to a queue
     * for logging or multi-threaded support.
     */
    public void update(Device device, String event) {
        try {
            // Create the requested Command
            Command storeCommand = factory.createCommand(getAdminToken(), event, device);

            // If Command was created, execute right away
            if (storeCommand != null) {
                storeCommand.execute();
            }
        } catch (StoreControllerServiceException e) {
            System.err.println(e);
        }

        return;
    }

    /** Returns the authentication token from the Controller. */
    public AuthToken getAdminToken() {
        return this.adminToken;
    }

    /**
     * Set the Admin Token to access restricted methods.
     *
     * @param adminToken    The AuthToken with admin privileges.
     */
    public void setAdminToken(AuthToken adminToken) {
        this.adminToken = adminToken;
    }

    /** Returns the Ledger associated with the Controller. */
    public Ledger getLedger() {
        return this.ledger;
    }

    /** Returns the StoreModelService associated with the Controller. */
    public StoreModelServiceInterface getStoreModel() {
        return this.storeModel;
    }
}