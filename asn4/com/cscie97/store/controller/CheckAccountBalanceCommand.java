package com.cscie97.store.controller;

import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;


/**
 * CheckAccountBalanceCommand.
 *
 * A CheckAccountBalanceCommand is created when a Customer asks a Microphone for
 * their basket’s current value. The Controller Service then calculates the
 * Basket’s cost, queries the Ledger Service for the Customer’s account balance,
 * and tells a Speaker to report back the result.
 *
 * @author Matthew Thomas
 */
public class CheckAccountBalanceCommand extends AbstractCommand {
    /** The Customer that requested their account balance. */
    private Customer customer;

    /** The Ledger which stores Accounts. */
    private Ledger ledger;

    /**
     * CheckAccountBalanceCommand Constructor.
     *
     * @param authToken     Token to authenticate with StoreModel API.
     * @param storeModel    Store Model Service to get/update state.
     * @param source        The Device which detected the event.
     * @param ledger        The Ledger which stores Account information.
     * @param customer      The Customer that requested their account balance.
     */
    public CheckAccountBalanceCommand(String authToken,
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
     * Calculate the basket total for the Customer, and compare against their
     * current account balance in the Ledger, including the minimum fee for a
     * transaction. Store the result in a message to be sent to the Customer
     * via a Speaker.
     *
     * @throws StoreControllerServiceException  If the Customer does not have a
     *                                          Basket, it cannot perform the
     *                                          lookup.
     */
    public void execute() throws StoreControllerServiceException {
        try {
            // Calculate total of all basket items
            Integer basketTotal = this.customer.calculateBasketTotal();

            // Check that Customer has a basket
            if (basketTotal < 0) {
                throw new StoreControllerServiceException(
                    "check account balance",
                    this.customer.customerName() + " does not have a basket."
                );
            }

            // Log the basket total
            System.out.println(this.customer.customerName() + " basket total is " + basketTotal);

            // Get account info from the ledger
            Integer minFee = this.ledger.getMinFee();
            Integer accountBalance = this.ledger.getAccountBalance(this.customer.getAccountAddress());

            // Compare account balance with basket total
            String balanceCheck;
            if (basketTotal + minFee < accountBalance) {
                balanceCheck = "less";
            } else {
                balanceCheck = "more";
            }

            // Report back to the Customer via speaker message
            String message = String.format(
                "Total value of basket items (and %d unit fee) is %d, which is %s than your account balance of %d",
                minFee, basketTotal + minFee, balanceCheck, accountBalance
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