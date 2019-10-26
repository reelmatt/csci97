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
 * CheckoutCommand.
 *
 * @author Matthew Thomas
 */
public class CheckoutCommand extends AbstractCommand {
    private Customer customer;

    public CheckoutCommand(String authToken, StoreModelServiceInterface storeModel, Device source, Customer customer) {
        super(authToken, storeModel, source);
        this.customer = customer;
    }


    public void execute() {
        try {
            Integer basketTotal = this.customer.calculateBasketTotal();

            if (basketTotal < 0) {
                throw new StoreControllerServiceException("checkout", this.customer.customerName() + " does not have a basket.");
            } else {
                System.out.println(this.customer.customerName() + " basket total is " + basketTotal);
            }

            Integer accountBalance = this.ledger.getAccountBalance(this.customer.getAccountAddress());

            if (basketTotal > accountBalance) {
                throw new StoreControllerServiceException("checkout", "The basket total exceeds your account balance. Please remove some items.");
            }



            Appliance speaker = super.getOneAppliance(ApplianceType.SPEAKER);
            super.sendCommand(speaker, message);


        } catch (LedgerException e) {
            System.err.println(e);
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;


        return;
    };
}