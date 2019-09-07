package com.cscie97.ledger;

public class Account {
    private String address;
    private Integer balance;

    public Account (String accountId) {
        this.address = accountId;
        this.balance = 0;
    }

    public Account (String accountId, Integer startingValue) {
        this.address = accountId;
        this.balance = startingValue;
    }

    public toString() {
        return this.address + " has a balance of " + this.balance;
    }
}