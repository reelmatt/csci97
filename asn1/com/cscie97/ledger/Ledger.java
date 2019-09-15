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

    /** Number of Transactions allowed per Block. */
    private static final int TRANSACTIONS_PER_BLOCK = 10;

    /** Maximum allowed account balance. */
    private static final int MAX_ACCOUNT_BALANCE = Integer.MAX_VALUE;

    /** List of accounts managed by the Ledger. */
    private List<String> accountList;

    /** Working block of the blockchain. */
    private Block currentBlock;

    /** A map of block numbers and the associated Blocks. */
    private Map<Integer, Block> blockMap;

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
        this.accountList = new ArrayList<String>();
        this.blockMap = new LinkedHashMap<Integer, Block>();

        // Create currentBlock (genesisBlock)
        this.currentBlock = new Block((this.blockMap.size() + 1), null);

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
        // Convience variable for throwing exceptions
        String action = "process transaction";

        // Get total transaction withdrawal
        int amount = transaction.getAmount();
        int fee = transaction.getFee();
        int withdrawal = amount + fee;

        // Check transaction is valid
        if ( transaction.payer.getBalance() < withdrawal ) {
            throw new LedgerException(action, "Insufficient balance.");
        } else if ( fee < transaction.getMinFee() ) {
            throw new LedgerException(action, "Minimum fee not provided.");
        } else if ( (amount + transaction.receiver.getBalance()) > MAX_ACCOUNT_BALANCE ) {
            throw new LedgerException(action, "Receiver's balance would exceed maximum allowed.");
        }

        // Valid transaction, Transfer funds
        transaction.payer.withdraw(withdrawal);
        transaction.receiver.deposit(amount);

        // Transfer transaction fee to master account
        Account master = this.currentBlock.getAccount("master");
        master.deposit(fee);

        // Add completed transaction to block
        int numberOfTransactions = this.currentBlock.addTransaction(transaction);

        // When transaction limit reached, commit block and create new one
        if (numberOfTransactions == TRANSACTIONS_PER_BLOCK) {

            // Validate current block
            if (this.currentBlock.validate()) {
                // Make transactions and account balances immutable
                Block completedBlock = this.currentBlock;
                completedBlock.commitBlock();

                // Add current block to the end of the blockMap
                this.blockMap.put((this.blockMap.size() + 1), completedBlock);

                // Create new block with accountBalanceMap copied from completedBlock
                this.currentBlock = new Block(this.blockMap.size() + 1, completedBlock);
            } else {
                throw new LedgerException(action, "Block failed to validate.");
            }
        }

        return transaction.getTransactionId();
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
    public Account validateAccount(String accountId) throws LedgerException {
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
     * @throws LedgerException  If no blocks have been committed yet, or the
     *                          account does not exist.
     */
    public int getAccountBalance(String address) throws LedgerException {
        Block lastBlock;
        Account account;

        // Retrieve last block
        try {
            lastBlock = getBlock(blockMap.size());
        } catch (LedgerException e) {
            // Catch exception that 'Block does not exist' and throw a more specific error
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
    public Block getBlock(int blockNumber) throws LedgerException {
        Block block;

        if ( (block = this.blockMap.get(blockNumber)) == null ) {
            throw new LedgerException("get block", "Block " + blockNumber + " does not exist.");
        }
        return block;

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
    public String validateTransactionId(String transactionId) throws LedgerException {

        Iterator<Map.Entry<Integer, Block>> blocks = listBlocks();

        // Check committed Blocks
        while( blocks.hasNext() ) {
            Map.Entry<Integer, Block> entry = blocks.next();
            Transaction transaction = entry.getValue().getTransaction(transactionId);

            if (transaction != null) {
                throw new LedgerException("validate transaction ID", "This transaction already exists.");
//                return transaction.getTransactionId();
            }
        }


        // Check currently in-progress block -- NOT SUPPOSED TO BE ALLOWED!?!?
        Transaction transaction = this.currentBlock.getTransaction(transactionId);
        if (transaction != null) {
            throw new LedgerException("validate transaction ID", "This transaction already exists.");
            //            return transaction.getTransactionId();
        }

        return transactionId;
    }


    public void validate() throws LedgerException {
        Iterator<Map.Entry<Integer, Block>> blocks = listBlocks();

        while ( blocks.hasNext() ) {
            Map.Entry<Integer, Block> entry = blocks.next();

            if ( ! entry.getValue().validate() ) {
                throw new LedgerException("validate", "Account balances do not add up.");
            }

        }

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
        return String.format("Name: %s\nDescription: %s\n", this.name, this.description);
    }
}