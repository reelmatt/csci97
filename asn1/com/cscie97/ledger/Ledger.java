package com.cscie97.ledger;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages the Blocks of the blockchain.
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

    /** The minimum allowed fee for any transaction. */
    private static final Integer MIN_FEE = 10;

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
     * input. Initializes the first block (known as the genesis block), the
     * master account, and initializes the master account balance.
     *
     * @param   name                Name of ledger.
     * @param   description         Ledger description.
     * @param   seed                Input to use as input to the hashing algorithm.
     * @throws  LedgerException     If it fails to create the 'master' account.
     *                              Will re-throw exception to indicate failure
     *                              to initialize Ledger.
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

    /** Returns the name of the Ledger. */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves an Account to add to a new Transaction.
     *
     * Check the current block and retrieve the account stored in its
     * accountBalanceMap, if one exists. This differs from behavior in
     * getAccountBalance() and getAccountBalances() methods, which return
     * information from the last completed block.
     *
     * @param   accountId           The account to retrieve.
     * @return                      Account with address matching 'accountId'.
     * @throws  LedgerException     If the account does not exist.
     */
    public Account getValidAccount(String accountId) throws LedgerException {
        Account account = this.currentBlock.getAccount(accountId);

        // The account does not exist in the current block
        if (account == null) {
            throw new LedgerException(
                    "get account",
                    "The account '" + accountId + "' does not exist."
            );
        }

        return account;
    }

    /** Returns Iterator of all entries in the block map. */
    public Iterator<Map.Entry<Integer, Block>> listBlocks () {
        return this.blockMap.entrySet().iterator();
    }

    /**
     * Create a new account.
     *
     * Checks requested 'accountId' is unique. If so, the 'accountId' is assigned
     * as the 'address' of the account, a unique identifier. The balance of the
     * account is set to 0. If it is the master account, the balance is set to
     * MAX_ACCOUNT_BALANCE, which is equal to Integer.MAX_VALUE.
     *
     * @param   accountId        Address of the account to create.
     * @return                   The account created by the Ledger.
     * @throws  LedgerException  If an account already exists with the given
     *                           'accountId'.
     */
    public Account createAccount(String accountId) throws LedgerException {
        // Check uniqueness of 'accountId'
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
     *
     * @param   transaction     The Transaction, containing all info, to process.
     * @return
     * @throws  LedgerException If the validateTransaction() throws the Exception.
     *                          If a block reaches 10 transactions, but block
     *                          validation fails.
     */
    public String processTransaction(Transaction transaction) throws LedgerException {

        // throws LedgerException on invalid transaction
        validateTransaction(transaction);

        // Add completed transaction to block
        int numberOfTransactions = this.currentBlock.addTransaction(transaction);

        // When transaction limit reached, validate and commit block and create new one
        if (numberOfTransactions == TRANSACTIONS_PER_BLOCK) {
            try {
                validateBlock(this.currentBlock);
            } catch (LedgerException e) {
                throw new LedgerException("process transaction", e.getReason());
            }

            // Make transactions and account balances immutable
            Block completedBlock = this.currentBlock;
            completedBlock.commitBlock(this.seed);

            // Add current block to the end of the blockMap
            this.blockMap.put((this.blockMap.size() + 1), completedBlock);

            // Create new block with accountBalanceMap copied from completedBlock
            this.currentBlock = new Block(this.blockMap.size() + 1, completedBlock);
        }

        return transaction.getTransactionId();
    }

    /**
     * Lookup a requested account balance from the last block in the blockchain.
     *
     * @param  address          Address of the account to lookup.
     * @return                  Balance of the account, with given address,
     *                          based on the most recent completed block.
     * @throws LedgerException  If no blocks have been committed yet, or the
     *                          account does not exist.
     */
    public Integer getAccountBalance(String address) throws LedgerException {
        // Convenience variable for throwing exceptions
        String action = "get account balance";

        Account account;
        Block lastBlock;

        try {
            // Retrieve most recent completed block
            lastBlock = getBlock(blockMap.size());
        } catch (LedgerException e) {
            // Catch 'Block does not exist' and throw more specific exception
            throw new LedgerException(action, "No blocks have been commited yet.");
        }

        // If the account does not exist, throw an Exception
        if ( (account = lastBlock.getAccount(address)) == null ) {
            throw new LedgerException(action, "Account does not exist.");
        }

        return account.getBalance();
    }

    /**
     * Collect a Map of account addresses and balances from the most recent
     * completed block.
     *
     * @return                  The account balance map for the most recent
     *                          completed block.
     * @throws LedgerException  If no blocks have been committed yet.
     */
    public Map<String, Integer> getAccountBalances() throws LedgerException {
        Block lastBlock;

        try {
            // Retrieve most recent completed block
            lastBlock = getBlock(blockMap.size());
        } catch (LedgerException e) {
            // Catch exception that 'Block does not exist' and throw a more specific error
            throw new LedgerException("get account balances", "No blocks have been committed yet.");
        }

        // Get accountBalanceMap from the last committed block
        Map<String, Account> accountMap = lastBlock.getBalanceMap();

        // Create new map to store values
        Map<String, Integer> accountBalancesMap = new HashMap<String, Integer>();

        // Iterate through accounts to retrieve their current balances.
        for (Map.Entry<String, Account> entry : accountMap.entrySet() ) {
            accountBalancesMap.put(entry.getKey(), entry.getValue().getBalance());
        }

        return accountBalancesMap;
    }

    /**
     * Retrieve a Block, specified by 'blockNumber', that has been committed to
     * the blockchain.
     *
     * @param   blockNumber         The Block to look for.
     * @return                      Block on success, otherwise null.
     * @throws  LedgerException     If the Block does not exist (or is not commited
     *                              to blockchain).
     */
    public Block getBlock(int blockNumber) throws LedgerException {
        Block block;

        if ( (block = this.blockMap.get(blockNumber)) == null ) {
            throw new LedgerException("get block", "Block " + blockNumber + " does not exist.");
        }
        return block;

    }

    /**
     * Check all commited Blocks for Transaction with given 'transactionId'.
     *
     * @param   transactionId   Transaction to search for.
     * @return                  Requested Transaction, if it exists.
     * @throws  LedgerException If the requested transaction does not exist in a
     *                          Block committed to the blockchain.
     */
    public Transaction getTransaction(String transactionId) throws LedgerException {
        Iterator<Map.Entry<Integer, Block>> blocks = listBlocks();

        // Check committed Blocks
        while( blocks.hasNext() ) {
            Block block = blocks.next().getValue();

            Transaction transaction = block.getTransaction(transactionId);

            // Transaction was found
            if (transaction != null) {
                return transaction;
            }
        }

        throw new LedgerException("get transaction", "Transaction " + transactionId + " does not exist.");
    }

    /**
     * Validates the current state of the blockchain.
     *
     * For each block that has been committed, check:
     *      1) the hash of the previous hash,
     *      2) ensure that account balances total the maximum value, and
     *      3) that each block has exactly 10 transactions.
     *
     * @throws LedgerException  If the current state of the blockchain is
     *                          invalid, failing one of the three tests listed.
     */
    public void validate() throws LedgerException {
        Iterator<Map.Entry<Integer, Block>> blocks = listBlocks();

        while ( blocks.hasNext() ) {
            Block block = blocks.next().getValue();
            validateBlock(block);
        }

        return;
    }

    /**
     * Validates the current state of the blockchain.
     *
     * For each block that has been committed, check:
     *      1) the hash of the previous hash,
     *      2) ensure that account balances total the maximum value, and
     *      3) that each block has exactly 10 transactions.
     *
     * @throws LedgerException  If the current state of the blockchain is
     *                          invalid, failing one of the three tests listed.
     */
    private void validateBlock(Block block) throws LedgerException {

        if ( ! block.validateTransactions() ) {
            throw new LedgerException("validate", "Invalid block. The block does not contain exactly 10 transactions.");
        }

        if ( ! block.validateAccountBalances() ) {
            throw new LedgerException("validate", "Invalid block. Total currency does not equal MAX_ACCOUNT_BALANCE.");
        }

        if ( ! block.validateHash(this.seed) ) {
            throw new LedgerException("validate", "Invalid block. Hashes do not match.");
        }

        System.out.print("valid block.\n");
    }

    /**
     * Validates a Transaction before adding to a Block.
     *
     * A valid transaction meets the following three conditions:
     *      1) Payer's account has a balance greater than, or equal to, the
     *         transaction amount and fee.
     *      2) The transaction fee is greater than, or equal to, the minimum
     *         required fee (MIN_FEE, defined in Transaction).
     *      3) Receiver's account will not exceed MAX_ACCOUNT_BALANCE after
     *         deposit of the transaction amount.
     *
     * @param transaction
     * @throws LedgerException
     */
    private void validateTransaction(Transaction transaction) throws LedgerException {
        // Check for transaction id uniqueness
        validateTransactionId(transaction.getTransactionId());

        // Get total transaction withdrawal
        int amount = transaction.getAmount();
        int fee = transaction.getFee();
        int withdrawal = amount + fee;

        // Get Accounts related to the transaction
        Account payer = transaction.getPayer();
        Account receiver = transaction.getReceiver();
        Account master = this.currentBlock.getAccount("master");

        // Convenience variable for throwing exceptions
        String action = "process transaction";

//        System.out.println(payer);
//        System.out.println("\t\twithdrawal: " + withdrawal);

        // Check transaction is valid
        if (withdrawal < 0) {
            throw new LedgerException(action, "Withdrawal exceeds total available currency.");
        } else if ( payer.getBalance() < withdrawal ) {
            throw new LedgerException(action, "Insufficient balance.");
        } else if ( fee < MIN_FEE ) {
            throw new LedgerException(action, "Minimum fee not provided.");
        } else if ( (amount + receiver.getBalance()) < 0 || (amount + receiver.getBalance()) > MAX_ACCOUNT_BALANCE) {
            throw new LedgerException(action, "Receiver's balance would exceed maximum allowed.");
        }

        // Valid transaction, Transfer funds
        payer.withdraw(withdrawal);
//        System.out.println(payer);

        receiver.deposit(amount);
//        System.out.println(receiver);

        master.deposit(fee);
//        System.out.println(master);

    }

    /**
     * Validate a given transaction id.
     *
     * Goes through all committed blocks and the block currently being added to.
     * If a transaction matching the id is found, return true (invalid ID - it is
     * already in use). If no transaction matches, return false (valid ID - it has
     * not been used yet).
     *
     * @param   transactionId   The id to check.
     * @return                  transactionId that was verified to be unused.
     * @throws  LedgerException If a transaction with 'transactionId' already exists.
     */
    private void validateTransactionId(String transactionId) throws LedgerException {
        // Create the Exception
        LedgerException e = new LedgerException("validate transaction ID", "This transaction already exists.");

        // Check committed Blocks
        Iterator<Map.Entry<Integer, Block>> blocks = listBlocks();
        while( blocks.hasNext() ) {
            Block block = blocks.next().getValue();

            if (block.getTransaction(transactionId) != null) {
                throw e;
            }
        }

        // Also check the current block's transactions
        if (this.currentBlock.getTransaction(transactionId) != null) {
            throw e;
        }

        return;
    }

    /** Overrides default toString() method. */
    public String toString() {
        return "Ledger: " + this.name;
    }
}