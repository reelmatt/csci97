package com.cscie97.ledger;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Ledger - Manages the Blocks of the blockchain.
 *
 * @author Matthew Thomas
 */
public class Ledger {
    /** Name of Ledger. */
    private String name;

    /** Ledger description. */
    private String description;

    /** Seed that is used as input to the hashing algorithm. */
    private String seed;

    /** Number of Blocks that have been created. */
    private Integer numberOfBlocks = 0;

    /** Number of Transactions allowed per Block. */
    private static final int TRANSACTIONS_PER_BLOCK = 10;

    /** List of accounts managed by the Ledger. */
    private Map<String, Account> accountList;
    // private List<Account> accountList;

    /** Initial Block of the blockchain. */
    private Block genesisBlock;
    private Block currentBlock;

    /** A map of block numbers and the associated Blocks. */
    public Map<Integer, Block> blockMap;

    /**
     * Ledger Constructor
     *
     * Sets the name, description, and seed for the blockchain using provided
     * input. Initializes the genesis block, the master account, and initializes
     * the master account balance.
     *
     * @param name          Name of ledger.
     * @param description   Ledger description.
     * @param seed          Input to use as input to the hashing algorithm.
     * @throws LedgerException
     */
    public Ledger(String name, String description, String seed) throws LedgerException {
        // Assign values to properties
        this.name = name;
        this.description = description;
        this.seed = seed;

        // Initialize internal trackers
        this.accountList = new HashMap<String, Account>();
        this.blockMap = new LinkedHashMap<Integer, Block>();

        // Create genesisBlock and set it as the currentBlock
        this.genesisBlock = new Block((this.blockMap.size() + 1), null);
        this.currentBlock = this.genesisBlock;

        // Try creating master account
        try {
            this.currentBlock.addAccount(createAccount("master"));
        } catch (LedgerException e) {
            throw new LedgerException("initialize ledger", e.getReason());
        }
    }

    /**
     * Create a new account.
     *
     * Assigns a unique identifier and sets the balance to 0. If it is the
     * master account, the balance is set to Integer.MAX_VALUE.
     *
     * @param   accountId   Name of the account.
     * @return              The account identifier assigned by the Ledger.
     * @throws LedgerException
     */
    public Account createAccount(String accountId) throws LedgerException {
        // Check if there is already an account 'accountId'
        if ( this.accountList.containsKey(accountId) ) {
            throw new LedgerException(
                "create account",
                "The account '" + accountId + "' already exists."
            );
        }

        // The accountId is unique, so create new account
        Account newAccount = new Account(accountId);

        // Update Ledger and Block lists with new account
        this.accountList.put(accountId, newAccount);
        this.currentBlock.addAccount(newAccount);

        return newAccount;
    }

    /**
     * Process a transaction.
     *
     * Validate the Transaction and if valid, add the Transaction to the current
     * Block and update the associated Account balances for the current Block.
     * Return the assigned transactionId. If the transaction is not valid, throw
     * a LedgerException.
     *
     * @param transaction
     * @return
     */
    public String processTransaction(Transaction transaction) throws LedgerException {
//        System.out.println("LEDGER: " + transaction.toString());

        if ( transaction.validate() ) {
//            System.out.println("LEDGER: valid transaction. transferring funds");
            transaction.payer.withdraw(transaction.amount);
            transaction.receiver.deposit(transaction.amount);
            this.accountList.get("master").deposit(transaction.fee);
        } else {
            throw new LedgerException("process transaction", "Invalid transaction.");
        }


        int numberOfTransactions = this.currentBlock.addValidTransaction(transaction);

        // When transaction limit reached, commit block and create new one
        if (numberOfTransactions == TRANSACTIONS_PER_BLOCK) {
            this.blockMap.put((this.blockMap.size() + 1), this.currentBlock);
            Block oldBlock = this.currentBlock;
            this.currentBlock = new Block((this.blockMap.size() + 1), oldBlock);
        }

        return transaction.getTransactionId();
    }

    /**
     *
     * @param accountId
     * @return
     */
    public Account getAccount(String accountId) throws LedgerException {
        Account account = this.accountList.get(accountId);

        if (account == null) {
            throw new LedgerException(
                "get account",
                "The account '" + accountId + "' does not exist."
            );
        }

        return account;
    }

    /**
     * Return the account balance for the Account with given address based on
     * the most recent completed block. If the Account does not exist, throw a
     * LedgerException.
     *
     * @param address
     * @return
     */
    public int getAccountBalance(String address) throws LedgerException {
        Account account;
        Block lastBlock;

        int blockNumber = blockMap.size();
        System.out.println("blockMap.size() is " + blockNumber);

        if ( (lastBlock = getBlock(blockNumber)) == null) {
            throw new LedgerException("get account balance", "No blocks have been commited yet.");
        }
//        System.out.println("block.toString() is " + lastBlock.toString());

        if ( (account = lastBlock.getAccount(address)) == null ) {
            throw new LedgerException("get account balance", "Account does not exist.");
        }

//        System.out.println("account.toString() is " + account.toString());
        return account.getBalance();
    }

    /**
     *
     * @return
     */
    public Map<String, Account> getAccountBalances() throws LedgerException {
//        Map<String, Integer> test = new LinkedHashMap<String, Integer>();
//        return test;
        if (this.blockMap.size() == 0) {
            throw new LedgerException("get account balances", "No blocks have been committed yet.");
        }
        return this.accountList;
    }

    /**
     *
     * @param blockNumber
     * @return Block on success, otherwise null.
     */
    public Block getBlock(int blockNumber) {
        return this.blockMap.get(blockNumber);
    }

    /**
     * Return the Transaction for the given transactionId.
     *
     * @param transactionId Transaction to search for.
     * @return Transaction, if it exists. Otherwise, null.
     */
    public Transaction getTransaction(String transactionId) throws LedgerException {
//        System.out.println("LEDGER: in getTransaction()");
        for( Map.Entry<Integer, Block> entry: this.blockMap.entrySet() ) {
            Block block = entry.getValue();
//            System.out.println("checking block - " + block);
            Transaction transaction = block.getTransaction(transactionId);
            if (transaction != null) {
                return transaction;
            }
        }

        Transaction transaction = this.currentBlock.getTransaction(transactionId);
        if (transaction != null) {
            return transaction;
        }

//        System.out.println("LEDGER: NO TRANSACTION FOUND.");
        return null;
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