package com.cscie97.ledger;

import java.io.Serializable;

/**
 * An individual account within the Ledger Service.
 *
 * An account contains an address that provides a unique identity for the
 * Account. The Account also contains a balance that represents the value of
 * the account. The account can only be updated by the Ledger Service.
 *
 * @author Matthew Thomas
 */
public class Account implements Serializable {
    /** Unique identifier of the Account. */
    private final String address;

    /** Balance which reflects total transfers to/from the account. */
    private Integer balance;

    /** Maximum allowed account balance. */
    private static final int MAX_ACCOUNT_BALANCE = Integer.MAX_VALUE;

    /**
     * Account Constructor
     *
     * The initial balance for all accounts is 0, except for the 'master'
     * account which is initialized with all available currency, defined as
     * Integer.MAX_VALUE, or 2,147,483,647 units.
     *
     * @param accountId Unique identifier to associate with the account.
     */
    public Account (String accountId) {
        this.address = accountId;

        // 'master' account gets all currency, otherwise init to 0
        this.balance = (accountId == "master") ? MAX_ACCOUNT_BALANCE : 0;
    }

    /**
     * Account Constructor
     *
     * Takes an existing Account object, and creates a new one based on its
     * information. Called by Block#commitBlock() in creating an immutable
     * accountBalanceMap.
     *
     * @param old   Old account to copy data from.
     * @see Block#commitBlock()
     */
    public Account (Account old) {
        this.address = old.getAddress();
        this.balance = old.getBalance();
    }

    /** Returns the Account's unique identifier.*/
    public String getAddress() {
        return this.address;
    }

    /** Returns the current balance of the Account. */
    public Integer getBalance() {
        return this.balance;
    }

    /** @param amount The value to add to the Account balance. */
    public void deposit(int amount) {
        this.balance += amount;
    }

    /** @param amount The value to deduct from the Account balance. */
    public void withdraw(int amount) {
        this.balance -= amount;
    }

    /** Overrides default toString() method. */
    public String toString() {
        return String.format("Account %s: current balance = %d", this.address, this.balance);
    }
}
