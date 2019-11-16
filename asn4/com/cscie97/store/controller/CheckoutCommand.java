package com.cscie97.store.controller;

import com.cscie97.ledger.Account;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.ledger.Transaction;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.authentication.AuthToken;
import com.cscie97.store.authentication.AuthenticationException;
import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.InvalidAuthTokenException;

/**
 * CheckoutCommand.
 *
 * A CheckoutCommand is created by a Turnstile when a Customer approaches on
 * the way out of the Store. When the customer approaches, the total cost of
 * the basket is calculated, a Transaction is created and then submitted to the
 * Ledger Service to be added to the blockchain. Once processed, and the
 * Transaction was validated, the Turnstile opens and the closest Speaker emits
 * a goodbye message.
 *
 * @author Matthew Thomas
 */
public class CheckoutCommand extends AbstractCommand {
    /** The Customer exiting the Store. */
    private Customer customer;

    /** The Ledger which processes transactions. */
    private Ledger ledger;

    /**
     * CheckoutCommand Constructor.
     *
     * @param authToken     Token to authenticate with StoreModel API.
     * @param storeModel    Store Model Service to get/update state.
     * @param source        The Device which detected the event.
     * @param ledger        The Ledger which stores Account information.
     * @param customer      The Customer that requested their account balance.
     */
    public CheckoutCommand(AuthToken authToken,
                           StoreModelServiceInterface storeModel,
                           Device source,
                           Ledger ledger,
                           Customer customer) {
        super(authToken, storeModel, source);
        this.customer = customer;
        this.ledger = ledger;
    }

    /**
     * {@inheritDoc}
     *
     * Calculate the Customer's basket total, pull their account balance and check
     * they can cover the basket plus the fee charged by the Ledger. If the
     * Customer can afford the purchase, create a Transaction, and submit it to
     * the Ledger. Then check the weight of the basket to see if the Customer
     * needs assistance to their car, tasking a Robot if the weight is over 10
     * lbs. End by opening the turnstile and sending a goodbye message over a
     * speaker.
     *
     * @throws StoreControllerServiceException
     */
    public void execute() throws StoreControllerServiceException{
        try {
            // Calculate the Basket total
            Integer basketTotal = this.customer.calculateBasketTotal();

            if (basketTotal < 0) {
                throw new StoreControllerServiceException(
                    "checkout",
                    this.customer.customerName() + " does not have a basket.");
            }

            System.out.println(this.customer.customerName() + " basket total is " + basketTotal);

            // Retrieve Account balance and fee from Ledger
            Integer minFee = this.ledger.getMinFee();
            Integer accountBalance = this.ledger.getAccountBalance(this.customer.getAccountAddress());

            // Check the Customer can afford the basket items
            if (basketTotal + minFee > accountBalance) {
                throw new StoreControllerServiceException(
                    "checkout",
                    "The basket total (with fee) exceeds your account balance. Please remove some items."
                );
            }

            // Create and submit the transaction the Ledger, logging the new ID
            Transaction transaction = createTransaction(basketTotal, minFee);
            String transactionId = this.ledger.processTransaction(transaction);
            System.out.println("Processed transaction #" + transactionId);

            // Check basket weight and generate AssistCustomerCommand if > 10 lbs
            Double basketWeight = this.customer.calculateBasketWeight();
            if (basketWeight > 10.0) {
                String event = String.format("customer %s assistance", this.customer.getId());
                super.getStoreModel().receiveEvent(super.getAuthToken(), super.getSource().getId(), event);
            }

            // Open turnstile and emit goodbye message
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

    /**
     * Create a Transaction to submit to the Ledger.
     *
     * @param   basketTotal The total cost of the Customer's basket.
     * @param   minFee      The minimum fee required by the Ledger.
     * @return              The new Transaction.
     */
    private Transaction createTransaction(Integer basketTotal, Integer minFee) {
        // Get remaining pieces required for Transaction
        String nextTransaction = this.ledger.nextTransactionId();
        Account payer = this.ledger.getExistingAccount(this.customer.getAccountAddress());
        Account receiver = this.ledger.getExistingAccount("master");

        // Create the new Transaction
        return new Transaction(nextTransaction, basketTotal, minFee, "tets transaction", payer, receiver);
    }
}