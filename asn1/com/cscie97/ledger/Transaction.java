package com.cscie97.ledger;

public class Transaction {
    private String transactionId;
    private Integer amount;
    private Integer fee;
    private String payload;

    public Transaction(Integer amount, Integer fee) {
        this.amount = amount;
        this.fee = fee;
    }
}