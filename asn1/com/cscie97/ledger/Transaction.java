package com.cscie97.ledger;

import java.io.Serializable;

/**
 * An individual Transaction within the Ledger Service.
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
    private Integer amount;

    /** Fee to be taken from the payer and added to the master account. */
    private Integer fee;

    /** An arbitrary string that may be up to 1024 characters. */
    private String payload;

    /** Account who is issuing the transaction. */
    private Account payer;

    /** Account to whom the amount of the transaction is deposited. */
    private Account receiver;

    /**
     * Transaction Constructor
     *
     * A transaction consists of six parameters that specify a unique identifier,
     * the amount and fee to be charged, the accounts to withdraw and deposit from,
     * as well as a payload, or arbitrary message. After a transaction is created,
     * it is processed by the Ledger which validates the exchange in currency.
     *
     * @param id        Unique identifier.
     * @param amount    Amount to be transferred.
     * @param fee       Fee to be charged and transferred to 'master' account.
     * @param payload   Arbitrary message for the transaction (limited to 1024 characters).
     * @param payer     Account to deduct funds from.
     * @param receiver  Account to deposit the amount in.
     */
    public Transaction(String id, int amount, int fee, String payload, Account payer, Account receiver) {
        this.transactionId = id;
        this.amount = amount;
        this.fee = fee;
        this.payload = (payload.length() < 1024) ? payload : payload.substring(0, 1023);
        this.payer = payer;
        this.receiver = receiver;
    }

    /** Returns the Transaction's unique identifier. */
    public String getTransactionId() {
        return this.transactionId;
    }

    /** Returns the amount of the transaction. */
    public int getAmount() {
        return this.amount;
    }

    /** Returns the fee associated with the transaction. */
    public int getFee() {
        return this.fee;
    }

    /** Returns the Account associated with the payer. */
    public Account getPayer() {
        return this.payer;
    }

    /** Returns the Account associated with the receiver. */
    public Account getReceiver() {
        return this.receiver;
    }

    /** Overrides default toString() method. */
    public String toString() {
        return String.format("Transaction %s: %s (%d units from %s to %s; %d unit fee)",
                this.transactionId, this.payload, this.amount,
                this.payer.getAddress(), this.receiver.getAddress(), this.fee);
    }
}