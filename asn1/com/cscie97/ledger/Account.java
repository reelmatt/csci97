package com.cscie97.ledger;

import java.io.Serializable;

/**
 * Account - An individual account within the Ledger Service.
 *
 * An account contains an address that provides a unique identify for the
 * Account. The Account also contains a balance that represents the value of
 * the account. The account can only be update by the Ledger Service.
 *
 * @author Matthew Thomas
 */
public class Account implements Serializable {
    /** Unique identifier of the Account. */
    private String address;

    /** Balance of the account which reflects total transfers to/from. */
    private Integer balance;

    /** Maximum allowed account balance. */
    private static final int MAX_ACCOUNT_BALANCE = Integer.MAX_VALUE;


    /**
     * Account Constructor
     *
     * The initial balance for all accounts is 0, except for the 'master'
     * account which is initialized with all available currency, defined as
     * Integer.MAX_VALUE, or 2147483647 units.
     *
     * @param accountId
     */
    public Account (String accountId) {
        this.address = accountId;

        if (accountId == "master") {
            this.balance = MAX_ACCOUNT_BALANCE;
        } else {
            this.balance = 0;
        }

    }

    /**
     * Account Constructor
     *
     * Takes an existing account object, and creates a new one based on it's
     * information.
     *
     * @TODO make separate variables that are final? Update getter methods to
     * choose the correct one?
     *
     * @param old
     */
    public Account (Account old) {
        this.address = old.getAddress();
        this.balance = old.getBalance();
    }

    /**
     * @return The Account address, its unique identifier.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * @param amount The value to deduct from the Account balance.
     */
    public void withdraw(int amount) {
        this.balance -= amount;
        return;
    }

    /**
     * @param amount The value to add to the Account balance.
     */
    public void deposit(int amount) {
        this.balance += amount;
        return;
    }

    /**
     * @return The current balance of the Account.
     */
    public Integer getBalance() {
        return this.balance;
    }

    /**
     * @return Overrides default toString() method.
     */
    public String toString() {
        return "Account " + this.address + ": current balance = " + this.balance;
    }
}