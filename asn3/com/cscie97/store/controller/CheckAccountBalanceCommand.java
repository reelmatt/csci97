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

    public void execute() throws StoreControllerServiceException {
        try {
            Integer basketTotal = this.customer.calculateBasketTotal();

            if (basketTotal < 0) {
                throw new StoreControllerServiceException(
                    "check account balance",
                    this.customer.customerName() + " does not have a basket."
                );
            } else {
                System.out.println(this.customer.customerName() + " basket total is " + basketTotal);
            }

            Integer minFee = this.ledger.getMinFee();
            Integer accountBalance = this.ledger.getAccountBalance(this.customer.getAccountAddress());

            String balanceCheck;
            if (basketTotal + minFee < accountBalance) {
                balanceCheck = "less";
            } else {
                balanceCheck = "more";
            }

            String message = String.format(
                    "Total value of basket items (and %d unit fee) is %d, which is %s than your account balance of %d",
                    minFee,
                    basketTotal + minFee,
                    balanceCheck,
                    accountBalance
            );

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