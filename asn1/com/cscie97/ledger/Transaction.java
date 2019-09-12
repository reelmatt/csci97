package com.cscie97.ledger;

public class Transaction {
    // Properties
    private String transactionId;
    public int amount;
    public int fee;
    private String payload;

    // Constants
    private static final Integer MIN_FEE = 10;

    // Associations
    public Account payer;
    public Account receiver;

    public Transaction(String id, int amount, int fee, String payload, Account payer, Account receiver) {
        this.transactionId = id;
        this.amount = amount;
        this.fee = fee;
        this.payload = payload;
        this.payer = payer;
        this.receiver = receiver;
    }

    public String getTransactionId() {
        return this.transactionId;
    }



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

    public String toString() {
        return "Transaction " + this.transactionId + " for amount " + this.amount + " and payload of " + this.payload;
    }
}