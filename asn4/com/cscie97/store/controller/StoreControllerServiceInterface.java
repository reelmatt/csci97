package com.cscie97.store.controller;

import com.cscie97.store.model.Observer;
import com.cscie97.store.authentication.AuthToken;

/**
 * Interface for a Store Controller Service.
 *
 * The interface follows the Observer pattern as described in Head First Design,
 * Chapter 2. The StoreControllerService is notified of Store events when the
 * Store Model Service (the Subject), calls the update() method in the Observer
 * interface.
 *
 * The Controller is responsible for parsing event notifications it receives
 * and generating Command objects to perform a set of actions in response to the
 * event.
 *
 * @see com.cscie97.ledger.*
 * @see com.cscie97.store.model.*
 *
 * @author Matthew Thomas
 */
public interface StoreControllerServiceInterface extends Observer {
    public void setAdminToken(AuthToken adminToken);
}