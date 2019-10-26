package com.cscie97.store.controller;

import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Basket;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.StoreModelServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 * EnterStoreCommand.
 *
 * @author Matthew Thomas
 */
public class EnterStoreCommand implements Command {
    public Device source;

    private StoreModelServiceInterface storeModel;

    private String authToken = "authToken";

    private Customer customer;

    private Ledger ledger;

    public EnterStoreCommand(Device source, StoreModelServiceInterface storeModel, Ledger ledger, String customerId) throws StoreModelServiceException{
        this.source = source;
        this.storeModel = storeModel;
        this.ledger = ledger;
        this.customer = this.storeModel.getCustomer(this.authToken, customerId);
    }

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