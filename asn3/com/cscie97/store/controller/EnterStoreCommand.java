package com.cscie97.store.controller;

import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Basket;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;

/**
 * EnterStoreCommand.
 *
 * An EnterStoreCommand is created by a Turnstile when a Customer approaches on
 * the way in to a Store. When detected, the Store Controller Service looks up
 * the Customer in the Store Model Service and checks their account balance in
 * the Ledger Service. If the Customer is registered and has a positive balance,
 * a Basket is assigned to the Customer, the Turnstile opens, and the closest
 * Speaker emits a welcome message.
 *
 * @author Matthew Thomas
 */
public class EnterStoreCommand extends AbstractCommand {
    /** The Customer entering the Store. */
    private Customer customer;

    /** The Ledger which tracks Accounts. */
    private Ledger ledger;

    /**
     * EnterStoreCommand Constructor.
     *
     * @param authToken     Token to authenticate with StoreModel API.
     * @param storeModel    Store Model Service to get/update state.
     * @param source        The Device which detected the event.
     * @param ledger        The Ledger which stores Account information.
     * @param customer      The Customer that requested their account balance.
     * @throws StoreModelServiceException
     */
    public EnterStoreCommand(String authToken,
                             StoreModelServiceInterface storeModel,
                             Device source,
                             Ledger ledger,
                             Customer customer) {
        super(authToken, storeModel, source);
        this.ledger = ledger;
        this.customer = customer;
    }

    /**
     * {@inheritDoc}
     *
     * Check the Customer's account balance and make sure they have money before
     * entering the Store. Assign them a Basket for shopping, open the turnstile,
     * and welcome them into the Store.
     *
     * @throws StoreControllerServiceException
     */
    public void execute() throws StoreControllerServiceException {
        try {
            // Check for positive account balance
            Integer balance = this.ledger.getAccountBalance(this.customer.getAccountAddress());

            if (balance < 0) {
                throw new StoreControllerServiceException("enter store", "Customer has a negative balance.");
            }

            // Assign customer a basket
            Basket basket;
            try {
                basket = this.storeModel.getBasket(authToken, customer.getId());
            } catch (StoreModelServiceException e) {
                basket = this.storeModel.defineBasket(authToken, customer.getId());
            }

            // Open turnstile and send hello message through Turnstile display
            this.storeModel.receiveCommand(authToken, this.source.getId(), "open");
            this.storeModel.receiveCommand(authToken, this.source.getId(), "Hello, " + this.customer.customerName() + ", welcome to " + this.source.getStore());
        } catch (LedgerException e) {
            System.err.println(e);
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}