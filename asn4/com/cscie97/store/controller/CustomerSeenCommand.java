package com.cscie97.store.controller;

import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.authentication.AuthToken;
import com.cscie97.store.authentication.AuthenticationException;
import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.InvalidAuthTokenException;

/**
 * CustomerSeenCommand.
 *
 * A CustomerSeenCommand is created when a Camera detects a Customer has moved
 * in the Store to a new Aisle.
 *
 * @author Matthew Thomas
 */
public class CustomerSeenCommand extends AbstractCommand {
    /** The Aisle the Customer is seen in. */
    private Aisle aisle;

    /** The Customer that was detected. */
    private Customer customer;

    /**
     * CustomerSeenCommand Constructor.
     *
     * @param   authToken   Token to authenticate with StoreModel API
     * @param   storeModel  StoreModel to get/update state.
     * @param   source      The Device which detected the event.
     * @param   customer    The Customer that was detected.
     * @param   aisle       The Aisle the mess is located in.
     */
    public CustomerSeenCommand(AuthToken authToken,
                               StoreModelServiceInterface storeModel,
                               Device source,
                               Customer customer,
                               Aisle aisle) {
        super(authToken, storeModel, source);
        this.aisle = aisle;
        this.customer = customer;
    }

    /**
     * {@inheritDoc}
     *
     * Use the StoreModelService API to update Customer location with form
     * <store>:<aisle>.
     */
    public void execute() {
        try {
            String location = super.getSource().getStore() + ":" + this.aisle.getId();
            super.getStoreModel().updateCustomer(super.getAuthToken(), this.customer.getId(), location);
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        } catch (AccessDeniedException e) {
            System.err.println(e);
        } catch (AuthenticationException e) {
            System.err.println(e);
        } catch (InvalidAuthTokenException e) {
            System.err.println(e);
        }

        return;
    };
}