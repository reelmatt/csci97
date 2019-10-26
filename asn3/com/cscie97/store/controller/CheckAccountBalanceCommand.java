package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.model.StoreModelServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 * CheckAccountBalanceCommand.
 *
 * @author Matthew Thomas
 */
public class CheckAccountBalanceCommand extends AbstractCommand {
    private Customer customer;

    private Ledger ledger;

    public CheckAccountBalanceCommand(String authToken, StoreModelServiceInterface storeModel, Device source, Ledger ledger,  Customer customer) {
        super(authToken, storeModel, source);
        this.ledger = ledger;
        this.customer = customer;
    }

    public void execute() {
        try {
            Integer basketTotal = this.customer.calculateBasketTotal();
            System.out.println(this.customer.customerName() + " basket total is " + basketTotal);

            Integer accountBalance = this.ledger.getAccountBalance(this.customer.getAccountAddress());

            String message;
            if (basketTotal < accountBalance) {
                message = "Total value of basket items is " + basketTotal + " which is less than your account balance of " + accountBalance;
            } else {
                message = "Total value of basket items is " + basketTotal + " which is more than your account balance of " + accountBalance;
            }

            Appliance speaker = super.getOneAppliance(ApplianceType.SPEAKER);
            super.sendCommand(speaker, message);


        } catch (LedgerException e) {
            System.err.println(e);
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}