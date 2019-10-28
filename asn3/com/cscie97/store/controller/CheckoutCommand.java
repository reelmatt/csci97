package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Customer;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.Transaction;
import com.cscie97.ledger.Account;
import com.cscie97.ledger.LedgerException;
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

    private Ledger ledger;

    public CheckoutCommand(String authToken, StoreModelServiceInterface storeModel, Ledger ledger, Device source, Customer customer) {
        super(authToken, storeModel, source);
        this.customer = customer;
        this.ledger = ledger;
    }


    public void execute() throws StoreControllerServiceException{
        try {
            Integer basketTotal = this.customer.calculateBasketTotal();

            if (basketTotal < 0) {
                throw new StoreControllerServiceException("checkout", this.customer.customerName() + " does not have a basket.");
            } else {
                System.out.println(this.customer.customerName() + " basket total is " + basketTotal);
            }

            Integer minFee = this.ledger.getMinFee();
            Integer accountBalance = this.ledger.getAccountBalance(this.customer.getAccountAddress());

            if (basketTotal + minFee > accountBalance) {
                throw new StoreControllerServiceException(
                    "checkout",
                    "The basket total (with fee) exceeds your account balance. Please remove some items."
                );
            }


            Account payer = this.ledger.getExistingAccount(this.customer.getAccountAddress());
            Account receiver = this.ledger.getExistingAccount("master");

            String nextTransaction = this.ledger.nextTransactionId();

            Transaction transaction = new Transaction(nextTransaction, basketTotal, minFee, "tets transaction", payer, receiver);

            String transactionId = this.ledger.processTransaction(transaction);

            System.out.println("Processed transaction #" + transactionId);


            // Check if Customer needs assistance to car
            Double basketWeight = this.customer.calculateBasketWeight();
            if (basketWeight > 10.0) {
                String event = String.format("customer %s assistance", this.customer.getId());
                super.getStoreModel().receiveEvent(super.getAuthToken(), super.getSource().getId(), event);
            }



            super.sendCommand(super.getSource().getId(), "Open");
            String message = "Goodbye " + this.customer.customerName() + ", thanks for shopping!";
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