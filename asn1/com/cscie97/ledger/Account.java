package com.cscie97.ledger;

public class Account {
    private String address;
    private Integer balance;

    public Account (String accountId) {
        this.address = accountId;

        if (accountId == "master") {
            this.balance = Integer.MAX_VALUE;
        } else {
            this.balance = 0;
        }

    }

    public Integer getBalance() {
        return this.balance;
    }

    public String toString() {
        return this.address + " has a balance of " + this.balance;
    }
}