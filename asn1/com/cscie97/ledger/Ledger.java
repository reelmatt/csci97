package com.cscie97.ledger;

//import com.cscie97.ledger.*;
import java.util.Map;

public class Ledger {
    private String name;
    private String description;
    private String seed;

    public Ledger(String name, String description, String seed) {
        this.name = name;
        this.description = description;
        this.seed = seed;

        Account master = new Account(master, Integer.MAX_VALUE);
        Block genesisBlock = new Block(1);
    }

    public String createAccount(String accountId) {
        Account newAccount = new Account(accountId);

        return accountId;
    }

    public void processTransaction(String transactionId) {
        return;
    }

    public int getAccountBalance(String address) {
        return 0;
    }

    public Map<String, int> getAccountBalances() {

    }

    public Block getBlock(int blockNumber) {

    }

    public Transaction getTransaction(String transactionId) {

    }

    public void validate() {
        return;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "Name: " + this.name + "\nDescription: " + this.description + "\nSeed: " + this.seed;
    }
}