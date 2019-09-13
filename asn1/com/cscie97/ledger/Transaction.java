package com.cscie97.ledger;

import java.io.Serializable;

/**
 * Transaction - An individual Transaction within the Ledger Service.
 *
 * A transaction contains an id, an amount, a fee, a payload, and
 * references a payer and a receiver account. The transaction ammount is
 * transferred from the payer's account balance to the reciever's account
 * balance. The transaction fee is transferred from the payer's account to the
 * master account. Transactions are aggregated within blocks.
 *
 * @author Matthew Thomas
 */
public class Transaction implements Serializable {
    /** Unique identifier for the transaction. */
    private String transactionId;

    /** Amount to be deducted from payer's account and added to the receiver's. */
    public int amount;

    /** Fee to be taken from the payer and added to the master account. */
    public int fee;

    /** The minimum allowed fee for any transaction. */
    private static final Integer MIN_FEE = 10;

    /** An arbitrary string that may be up to 1024 characters. */
    private String payload;

    /** Account who is issuing the transaction. */
    public Account payer;

    /** Account to whom the amount of the transaction is deposited. */
    public Account receiver;

    /**
     * Transaction Constructor
     *
     * @param id        Unique identifier.
     * @param amount    Amount to be transferred.
     * @param fee       Fee to be charged and transferred to 'master' account.
     * @param payload   Arbitrary message for the transaction.
     * @param payer     Account to deduct funds from.
     * @param receiver  Account to deposit the amount in.
     */
    public Transaction(String id, int amount, int fee, String payload, Account payer, Account receiver) {
        this.transactionId = id;
        this.amount = amount;
        this.fee = fee;
        this.payload = payload;
        this.payer = payer;
        this.receiver = receiver;
    }

    /**
     * @return The Transaction id, its unique identifier.
     */
    public String getTransactionId() {
        return this.transactionId;
    }

    /**
     *
     * @return True, if valid transaction. Otherwise, false.
     */
    public Boolean validate() {
        int withdrawal = this.amount + this.fee;

        if (payer.getBalance() < withdrawal) {
            // Payer does not have enough funds
        } else if (this.fee < MIN_FEE) {
            // The fee is less than the minimum
        } else {
            // It is a valid transaction
            return true;
        }

        return false;
    }

    /**
     * @return Overrides default toString() method.
     */
    public String toString() {
        return String.format("Transaction %s: %s (%d units from %s to %s; %d unit fee)",
                this.transactionId, this.payload, this.amount,
                this.payer.getAddress(), this.receiver.getAddress(), this.fee);
    }
}