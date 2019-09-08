package com.cscie97.ledger;

import java.util.Map;
import java.util.LinkedHashMap;

public class Ledger {
    // Properties
    private String name;
    private String description;
    private String seed;

    // Associations
    private Block genesisBlock;
    private Block currentBlock;
    private Map<Integer, Block> blockMap;

    // Constructor
    public Ledger(String name, String description, String seed) {
        // Assign values to properties
        this.name = name;
        this.description = description;
        this.seed = seed;

        // Initialize blockMap with genesisBlock
        this.blockMap = new LinkedHashMap<Integer, Block>();
        this.genesisBlock = new Block(1);
        this.currentBlock = this.genesisBlock;
        System.out.println("Genesis block is: " + this.genesisBlock.toString());
//        this.blockMap.put(1, genesisBlock);
    }

    // Methods
    public String createAccount(String accountId) {
        Account newAccount = new Account(accountId);

        this.currentBlock.addAccount(newAccount);


        return accountId;
    }

    public void processTransaction(String transactionId) {
        return;
    }

    public int getAccountBalance(String address) {
        return 0;
    }

    public Map<String, Integer> getAccountBalances() {
        Map<String, Integer> test = new LinkedHashMap<String, Integer>();
        return test;
    }

    public Block getBlock(int blockNumber) {
        return this.blockMap.get(blockNumber);
    }

    public Transaction getTransaction(String transactionId) {
        return new Transaction(100, 10);
    }

    public void validate() {
        return;
    }

    public String getName() {
        return this.name;
    }

    public Integer numberOfBlocks() {
        return this.blockMap.size();
    }
    public String toString() {
        return "Name: " + this.name + "\nDescription: " + this.description + "\nAccount: " + this.genesisBlock.getAccount("master") + "\n";
    }
}