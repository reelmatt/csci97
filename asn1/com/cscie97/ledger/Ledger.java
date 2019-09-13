package com.cscie97.ledger;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// For serialziation
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * Ledger - Manages the Blocks of the blockchain.
 *
 * The Ledger manages the transactions, accounts, and blocks that make up the
 * Blockchain. Users submit transactions which, once validated, are added to a
 * block. As Blocks fill up with Transactions, Account balances are updated,
 * and the Blocks are added to the Ledger. Once committed to the Ledger, a
 * Block, and the contained Transactions and Account balances are immutable.
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
//    private Map<String, Account> accountList;
     private List<String> accountList;

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
        // this.accountList = new HashMap<String, Account>();
        this.accountList = new ArrayList<String>();
        this.blockMap = new LinkedHashMap<Integer, Block>();

        // Create genesisBlock and set it as the currentBlock
        this.genesisBlock = new Block((this.blockMap.size() + 1), null);
        this.currentBlock = this.genesisBlock;

        // Try creating master account
        try {
            Account master = createAccount("master");
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
     * @param   accountId       Name of the account.
     * @return                  The account identifier assigned by the Ledger.
     * @throws LedgerException  If an account already exists with the given
     *                          'accountId'.
     */
    public Account createAccount(String accountId) throws LedgerException {
        // Check if there is already an account 'accountId'
        if ( this.accountList.contains(accountId) ) {
            throw new LedgerException(
                "create account",
                "The account '" + accountId + "' already exists."
            );
        }

        // The accountId is unique, so create new account
        Account newAccount = new Account(accountId);

        // Update Ledger and Block lists with new account
        this.accountList.add(accountId);
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
        // Get total withdrawal amount
        int withdrawal = transaction.getAmount() + transaction.getFee();

        // Validate transaction
        if ( transaction.payer.getBalance() < withdrawal ) {
            throw new LedgerException("process transaction", "Insufficient balance.");
        } else if ( transaction.fee < transaction.getMinFee() ) {
            throw new LedgerException("process transaction", "Minimum fee not provided.");
        }

        // Otherwise, a valid transaction
        // Transfer funds
        transaction.payer.withdraw(transaction.amount + transaction.fee);
        transaction.receiver.deposit(transaction.amount);

        Account master = this.currentBlock.getAccount("master");
        master.deposit(transaction.fee);

        // Add completed transaction to block
        int numberOfTransactions = this.currentBlock.addTransaction(transaction);

        // When transaction limit reached, commit block and create new one
        if (numberOfTransactions == TRANSACTIONS_PER_BLOCK) {

            ByteArrayOutputStream bos = serializeObject(this.currentBlock);
            Block clone = (Block) deserializeObject(bos);


            this.blockMap.put((this.blockMap.size() + 1), this.currentBlock);
            this.currentBlock = clone;
            this.currentBlock.clearTransactions();

//            this.currentBlock = new Block((this.blockMap.size() + 1), clone);
        }

        return transaction.getTransactionId();
    }

    private ByteArrayOutputStream serializeObject(Object object) {
        System.out.println("IN SERIALIZE, object is " + object);
        ByteArrayOutputStream bos = null;

        try {
            // Create byte array output stream and use it to create object output stream
            bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(object);		// serialize
            oos.flush();
        } catch (IOException e) {
//                System.out.println("serialize io");
//                System.out.println(e);
        }
        return bos;
    }

    private Object deserializeObject(ByteArrayOutputStream b) {
        Object clone = null;

        try {
            // toByteArray creates & returns a copy of stream’s byte array
            byte[] bytes = b.toByteArray();

            // Create byte array input stream and use it to create object input stream
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

            ObjectInputStream ois = new ObjectInputStream(bis);
            clone = ois.readObject();		// deserialize & typecast


        } catch (IOException e) {
//                System.out.println("deserialize io");
        } catch (ClassNotFoundException e) {
//                System.out.println("deserialize class not found");
        }

        return clone;

    }

    /**
     * Internal Ledger Service method to obtain the most up-to-date Account
     * object. This differs from behavior in public methods getAccountBalance()
     * and getAccountBalances() which return information from the last completed
     * block.
     *
     * @param accountId         The account to retrieve.
     * @return                  Account with address matching 'accountId'.
     * @throws LedgerException  If the account does not exist.
     */
    public Account getAccount(String accountId) throws LedgerException {
        // Account comes from the current block
        Account account = this.currentBlock.getAccount(accountId);

        // If account does not exist, throw exception
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
     * @param address           Address of the account to lookup.
     * @return                  Current balance of the account, in the last
     *                          committed block.
     * @throws LedgerException  If no blocks have been committed yet, or th
     *                          account does not exist.
     */
    public int getAccountBalance(String address) throws LedgerException {
        Block lastBlock;
        Account account;
        int blockNumber = blockMap.size();

        // If getBlock() returns null, no blocks have been committed to lookup account in
        if ( (lastBlock = getBlock(blockNumber)) == null) {
            throw new LedgerException("get account balance", "No blocks have been commited yet.");
        }

        // If the account does not exist, throw an Exception
        if ( (account = lastBlock.getAccount(address)) == null ) {
            throw new LedgerException("get account balance", "Account does not exist.");
        }

        return account.getBalance();
    }

    /**
     * @return                  The account balance map for the most recent
     *                          completed block.
     * @throws LedgerException  If no blocks have been committed yet.
     */
    public Map<String, Integer> getAccountBalances() throws LedgerException {
        // Retrieval occurs from last committed block.
        int blockNumber = this.blockMap.size();

        // If no blocks, throw exception
        if (blockNumber == 0) {
            throw new LedgerException("get account balances", "No blocks have been committed yet.");
        }

        // Get accountBalanceMap from the last committed block
        Map<String, Account> accountMap = getBlock(blockNumber).getBalanceMap();

        // Create new map to store values
        Map<String, Integer> accountBalancesMap = new HashMap<String, Integer>();

        // Iterate through accounts to retrieve their current balances.
        for (Map.Entry<String, Account> entry : accountMap.entrySet() ) {
            accountBalancesMap.put(entry.getKey(), entry.getValue().getBalance());
        }

        return accountBalancesMap;
    }

    /**
     * @return Iterator to get values from blockMap
     */
    public Iterator<Map.Entry<Integer, Block>> listBlocks () {
        return this.blockMap.entrySet().iterator();
    }

    /**
     * Retrieve a Block committed to the blockchain specified by 'blockNumber'.
     * @param   blockNumber The Block to look for.
     * @return              Block on success, otherwise null.
     */
    public Block getBlock(int blockNumber) {
        return this.blockMap.get(blockNumber);
    }

    /**
     * Return the Transaction for the given transactionId. Checks all commited
     * Blocks.
     *
     * @param transactionId Transaction to search for.
     * @return Transaction, if it exists. Otherwise, null.
     */
    public Transaction getTransaction(String transactionId) throws LedgerException {
        Iterator<Map.Entry<Integer, Block>> blocks = listBlocks();

        // Check committed Blocks
        while( blocks.hasNext() ) {
            Map.Entry<Integer, Block> entry = blocks.next();
            Transaction transaction = entry.getValue().getTransaction(transactionId);

            // Transaction was found
            if (transaction != null) {
                return transaction;
            }
        }

        throw new LedgerException("get transaction", "Transaction " + transactionId + " does not exist.");
    }

    /**
     * Validate a given transaction id.
     *
     * Goes through all committed blocks and the block currently being added to.
     * If a transaction matching the id is found, return true (invalid ID - it is
     * already in use). If no transaction matches, return false (valid ID - it has
     * not been used yet).
     *
     * @param transactionId
     * @return
     */
    public Boolean validateTransactionId(String transactionId) {

        Iterator<Map.Entry<Integer, Block>> blocks = listBlocks();

        // Check committed Blocks
        while( blocks.hasNext() ) {
            Map.Entry<Integer, Block> entry = blocks.next();
            Transaction transaction = entry.getValue().getTransaction(transactionId);

            if (transaction != null) {
                return true;
                // return transaction;
            }
        }

//        throw new LedgerException("get transaction", "Transaction " + transactionId + " does not exist.");


        // Check currently in-progress block -- NOT SUPPOSED TO BE ALLOWED!?!?
        Transaction transaction = this.currentBlock.getTransaction(transactionId);
        if (transaction != null) {
            return true;
            // return transaction;
        }

        // return null;
        return false;
    }


    public void validate() {
        return;
    }

    /**
     * @return Name of Ledger.
     */
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