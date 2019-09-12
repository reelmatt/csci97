package com.cscie97.ledger;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

public class Ledger {
    // Properties
    private String name;
    private String description;
    private String seed;

    // Added properties (defaults)
    private Integer minimumFee = 10;
    private Integer numberOfBlocks = 0;
//    private List<Account> accountList;
    private Map<String, Account> accountList;

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

        // Initialize internal trackers
//        this.accountList = new ArrayList<Account>();
        this.accountList = new HashMap<String, Account>();
        this.blockMap = new LinkedHashMap<Integer, Block>();

        // Create genesisBlock
        this.genesisBlock = new Block(1, null);
        this.currentBlock = this.genesisBlock;
        System.out.println("Genesis block is: " + this.genesisBlock.toString());

        // Try creating master account
        try {
            Account newAccount = createAccount("master");
            this.currentBlock.addAccount("master", newAccount);
        } catch (LedgerException e) {
            System.out.print("Oops. There was an issue creating the `master` account.");
        }
    }

    // Methods
    public Account createAccount(String accountId) throws LedgerException {
        // Check if there is already an account 'accountId'
        if ( this.accountList.containsKey(accountId) ) {
            System.out.println("An account with ID " + accountId + " already exists.");
            //return the exception instead?
            throw new LedgerException();

        }

        // accountId doesn't already exist, so create account and add to List
        Account newAccount = new Account(accountId);

//        this.accountList.add(newAccount);
        this.accountList.put(accountId, newAccount);
        System.out.println("LEDGER: Creating new account " + accountId);

        return newAccount;
    }

    public String processTransaction(Transaction transaction) {
        System.out.println("LEDGER: " + transaction.toString());
        //        Use this code after genesisBlock has 10 transactions
//        this.blockMap.put(1, genesisBlock);
        return "1";
    }

    public Account getAccount(String accountId) {
        System.out.println("In getAccount, id is " + accountId);
//        int account = this.accountList.indexOf(accountId);
//        return this.accountList.get(0);
        return this.accountList.get(accountId);
    }

    public int getAccountBalance(String address) {
        Account account = this.accountList.getAccount(address);
        return account.getBalance();
    }

//    public Map<String, Account> getAccountBalances() {
      public Map<String, Integer> getAccountBalances() {
//        Map<String, Integer> test = new LinkedHashMap<String, Integer>();
//        return test;
        return this.accountList;
    }

    public Block getBlock(int blockNumber) {
        return this.blockMap.get(blockNumber);
    }

//    public Transaction getTransaction(String transactionId) {
//        return new Transaction("1", 100, 10, "test payload");
//    }

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