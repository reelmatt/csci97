package com.cscie97.ledger;

public class Transaction {
    // Properties
    private String transactionId;
    private int amount;
    private int fee;
    private String payload;

    // Associations
    private Account payer;
    private Account receiver;

    public Transaction(String id, int amount, int fee, String payload, Account payer, Account receiver) {
        this.transactionId = id;
        this.amount = amount;
        this.fee = fee;
        this.payload = payload;
        this.payer = payer;
        this.receiver = receiver;
    }



    public String toString() {
        return "Transaction " + this.transactionId + " for amount " + this.amount + " and payload of " + this.payload;
    }
}