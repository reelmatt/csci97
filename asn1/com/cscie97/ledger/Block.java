package com.cscie97.ledger;

import java.util.Map;
import java.util.HashMap;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * Block - Aggregates groups of transactions in the blockchain.
 *
 * Transactions are added to blocks in the order that they are received. Prior
 * to adding a transaction to a block, the transaction must be validated.
 * The block contains an account balance map that reflects the balance of all
 * accounts after all the transactions within the block have been applied. The
 * block contains the hash of the previous block and the hash of itself.
 *
 * @author Matthew Thomas
 */
public class Block implements Serializable {
    /** A sequentially incrementing block number assigned to the block. */
    private Integer blockNumber;

    /** The hash of the previous block in the blockchain. */
    private String previousHash;

    /** The hash of the current block, based on all properties and associations. */
    private String hash;

    /** An ordered list of Transactions that are included in the current block. */
    private List<Transaction> transactionList;

    /** Full set of accounts managed by the Ledger. */
    private Map<String, Account> accountBalanceMap;

    /** References the preceding Block in the blockchain. */
    private Block previousBlock;

    /** Maximum allowed account balance. */
    private static final int MAX_ACCOUNT_BALANCE = Integer.MAX_VALUE;

    /** Number of Transactions allowed per Block. */
    private static final int TRANSACTIONS_PER_BLOCK = 10;

    /**
     * Block Constructor
     *
     * Sequentially numbered blocks, aggregates transaction and account
     * information. A Block also contains a reference to the previous block in
     * the chain (except for the first block). When a Block is committed to the
     * chain, a hash is generated using all properties and associations and the
     * SHA-256 algorithm.
     *
     * @param number        Block number (ID)
     * @param previousBlock The previousBlock in the chain. Contains copy of
     *                      accounts and balances, and the Object is used to
     *                      check the previousHash.
     */
    public Block (int number, Block previousBlock) {
        this.blockNumber = number;
        this.previousBlock = previousBlock;

        this.hash = null;
        this.previousHash = (previousBlock != null) ? previousBlock.getHash() : null;

        // Create new, empty, lists
        this.transactionList = new ArrayList<Transaction>();
        this.accountBalanceMap = new HashMap<String, Account>();

        // If linking to a previous block, deep copy the accountBalanceMap
        // src: https://www.baeldung.com/java-deep-copy
        if (previousBlock != null) {
            for ( Map.Entry<String, Account> entry : previousBlock.getBalanceMap().entrySet() ) {
                Account oldAccount = entry.getValue();
                Account newAccount = new Account(oldAccount);
                accountBalanceMap.put(entry.getKey(), newAccount);
            }
        }
    }

    /** Returns the block's number. */
    public Integer getBlockNumber() {
        return this.blockNumber;
    }

    /** Returns the hash of this block. */
    public String getHash() {
        return this.hash;
    }

    /** Returns the hash of the previous block. */
    public String getPreviousHash() {
        return this.previousHash;
    }

    /** Returns the list of transactions. */
    public List<Transaction> getTransactionList() {
        return this.transactionList;
    }

    /** Returns map of account addresses and Account objects. */
    public Map<String, Account> getBalanceMap() {
        return this.accountBalanceMap;
    }

    /**
     * Returns the Account matching 'address'.
     *
     * @param address The account to lookup in the map.
     */
    public Account getAccount(String address) {
        return this.accountBalanceMap.get(address);
    }

    /**
     * Creates a new entry in the accountBalanceMap.
     *
     * @param account The account to add to the current balance map.
     */
    public void addAccount(Account account) {
        this.accountBalanceMap.put(account.getAddress(), account);
    }

    /**
     * Locate the given transaction within the Block's transactionList.
     *
     * @param   transactionId   The Transaction to find.
     * @return                  Requested Transaction if exists. Otherwise, null.
     */
    public Transaction getTransaction(String transactionId) {
        for (Transaction transaction : transactionList) {
            if ( transactionId.equals(transaction.getTransactionId()) ) {
                return transaction;
            }
        }

        return null;
    }

    /**
     * Add valid transaction to list.
     *
     * @param   transaction     Transaction to add to the running list.
     * @return                  Current number of transactions in the block.
     */
    public int addTransaction(Transaction transaction) {
        this.transactionList.add(transaction);
        return this.transactionList.size();
    }

    /**
     * Validates that transaction list contains the correct number of entries.
     *
     * @return  True if list contains correct number of entries. Otherwise, false.
     */
    public Boolean validateTransactions() {
        return transactionList.size() == TRANSACTIONS_PER_BLOCK;
    }

    /**
     * Validates the sum of all account balances equals the total Ledger currency.
     *
     * @return  True if all account balances equals total currency. Otherwise, false.
     */
    public Boolean validateAccountBalances() {
        Integer totalCurrency = 0;

        // Iterate through accounts to retrieve their current balances.
        for (Map.Entry<String, Account> entry : this.accountBalanceMap.entrySet() ) {
            totalCurrency += entry.getValue().getBalance();
        }

        // True (valid) when total currency adds up to MAX_ACCOUNT_BALANCE
        return totalCurrency == MAX_ACCOUNT_BALANCE;

    }

    /**
     * Validates that the previous block's hash equals the stored value of said hash.
     *
     * @param   seed    The Ledger's seed value, used as input to the hash.
     * @return          True, if the hashes match. Otherwise, false.
     */
    public Boolean validateHash(String seed) {
        // Ignore the first (genesis) block
        if (this.previousBlock != null) {
            // Recompute the hash
            String checkHash = computeHash(this.previousBlock, seed);

            // Compare it with the stored value
            return checkHash.equals(this.previousHash);
        }

        return true;
    }

    /**
     * Freeze the current state of the block before commiting to the blockchain.
     *
     * Citations:
     * src: https://dev.to/monknomo/make-an-immutable-object---in-java-480n
     *
     * @param seed The Ledger's seed value, used as input to the hash.
     */
    public void commitBlock(String seed) {
        // Deep copy accountBalanceMap to break references
        Map<String, Account> immutableBalanceMap = new HashMap<String, Account>();
        immutableBalanceMap.putAll(this.accountBalanceMap);

        // Copy associations to immutable collections
        this.transactionList = Collections.unmodifiableList(this.transactionList);
        this.accountBalanceMap = Collections.unmodifiableMap(immutableBalanceMap);

        // Calculate the hash for the block
        this.hash = computeHash(this, seed);
        return;
    }

    /**
     * Compute hash of block.
     *
     * For all non-null Blocks, serialize the Block (including its properties
     * and associations, excluding the block's own hash) and the Ledger seed
     * as a salt. The output is then hashed, using SHA-256 and converted to a
     * String.
     *
     * @param   blockToHash Block object to hash.
     * @param   seed        Ledger's seed value to use as input to hash.
     * @return              Hash of block, converted to a String.
     */
    private String computeHash(Block blockToHash, String seed) {
        // If no block to hash, ignore (the genesis block)
        if (blockToHash == null ) {
            return null;
        }

        // Convert Block to a byte[], to hash with SHA-256
        byte[] toHash = blockToBytes(blockToHash, seed);

        // Convert hash to String
        return hashToString(toHash);
    }

    /**
     * Generate a hash and convert to a String. Use SHA-256.
     *
     * Citations:
     * src: https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
     * src: https://www.baeldung.com/sha-256-hashing-java
     *
     * @param   toHash  The byte array to hash.
     * @return          String representation of hash.
     */
    private String hashToString(byte[] toHash) {
        // Initialize a SHA-256 hasher
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.toString());
        }

        // Hash the byte-ified Block object
        byte[] encodedHash = digest.digest(toHash);

        // Converting byte[] to String
        // src: https://howtodoinjava.com/array/convert-byte-array-string-vice-versa/
        return Base64.getEncoder().encodeToString(encodedHash);
    }

    /**
     * Serialize a Block.
     *
     * Helper method to serialize the requested parts of a Block object, such
     * that it can be hashed with SHA-256. The hash, and therefore the contents
     * of the byte[] include the following:
     *      1) Ledger's seed value,
     *      2) blockNumber,
     *      3) previousHash,
     *      4) transactionList,
     *      5) accountBalanceMap,
     *
     * Citations:
     * Creating a byte array:
     * src: https://stackoverflow.com/questions/5368704/appending-a-byte-to-the-end-of-another-byte/5368731
     *
     * @param   blockToHash Block object to serialize.
     * @param   seed        Ledger's seed value.
     * @return              Block + seed converted to a byte[].
     */
    private byte[] blockToBytes(Block blockToHash, String seed) {
        // Initialize stream
        ByteArrayOutputStream block = new ByteArrayOutputStream();

        try {
            // Convert Ledger seed value
            block.write(seed.getBytes());

            // Convert Block properties
            if (blockToHash.getBlockNumber() != null ) {
                block.write(blockToHash.getBlockNumber().byteValue());
            }

            if (blockToHash.getPreviousHash() != null) {
                block.write(blockToHash.getPreviousHash().getBytes());
            }

            // Convert Block associations
            block.write(serializeToArray( blockToHash.getTransactionList() ));
            block.write(serializeToArray( blockToHash.getBalanceMap() ));
        } catch (IOException e) {
            System.err.println(e);
        }

        // Convert to byte[]
        return block.toByteArray();
    }

    /**
     * Serialize an Object into an array of bytes.
     *
     * This helper method converts an Object which implements Serializable into
     * a byte[]. This is called by blockToBytes() to convert a Block's
     * transaction list and account balance map such that they can be hashed.
     *
     * @see blockToBytes
     *
     * @param   object  The Object to convert to bytes.
     * @return          The resulting byte[].
     */
    private byte[] serializeToArray(Object object) {
        ByteArrayOutputStream bos = null;

        try {
            // Create byte array output stream to use with the object output stream
            bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            // Serialize 'object'
            oos.writeObject(object);
            oos.flush();
        } catch (IOException e) {
            System.err.println(e);
        }

        return bos.toByteArray();
    }

    /** Overrides default toString() method. */
    public String toString() {
        String separator = "+-----------------------------------------------\n";

        // Print Block header with properities
        String block = separator;
        block += String.format("| Block #%d\n", this.blockNumber);
        block += String.format("|  previousHash: %s\n", this.previousHash);
        block += String.format("| previousBlock: %d\n", (this.previousBlock == null) ? null : this.previousBlock.getBlockNumber() );
        block += String.format("|          hash: %s\n", this.hash);
        block += separator;

        // Print transaction list
        for (Transaction transaction : transactionList) {
            block += String.format("| %s\n", transaction);
        }

        block += separator;

        // Print account balances
        for( Map.Entry<String, Account> entry: this.accountBalanceMap.entrySet() ) {
            block += String.format("| %s\n", entry.getValue());
        }

        // Close Block output
        block += separator;
        return block;
    }
}