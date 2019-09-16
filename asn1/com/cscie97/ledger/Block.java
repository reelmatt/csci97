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

    /** Minimum allowed account balance. */
    private static final int MIN_ACCOUNT_BALANCE = 0;

    /** Maximum allowed account balance. */
    private static final int MAX_ACCOUNT_BALANCE = Integer.MAX_VALUE;

    /**
     * Block Constructor
     *
     * @param number        Block number (ID)
     * @param previousBlock Contents of previousBlock (for hashing)
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


    /** Returns the account-balance map. */
    public Map<String, Account> getBalanceMap() {
        return this.accountBalanceMap;
    }

    /**
     * @param account The account to add to the current balance map.
     */
    public void addAccount(Account account) {
        this.accountBalanceMap.put(account.getAddress(), account);
    }

    /** Returns the account matching 'address'. */
    public Account getAccount(String address) {
        return this.accountBalanceMap.get(address);
    }

    /** Returns the hash of this block. */
    public String getHash() {
        return this.hash;
    }

    /** Returns the hash of the previous block. */
    public String getPreviousHash() {
        return this.previousHash;
    }

    /** Returns the previous block. */
    public Block getPreviousBlock() {
        return this.previousBlock;
    }

    /** Returns the list of transactions. */
    public List<Transaction> getTransactionList() {
        return this.transactionList;
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


    public Transaction getTransaction(String transactionId) {

        for (Transaction transaction : transactionList) {
            if ( transactionId.equals(transaction.getTransactionId()) ) {
                return transaction;
            }
        }

        // no transaction found
        return null;
    }

    /**
     * Display
     * @return
     */
    public String toString() {
        String separator = "+-------------------------------------\n";
        String block = separator;
        block += String.format("| Block #%d\n", this.blockNumber);
        block += String.format("| previousHash: %s\n", this.previousHash);
        block += String.format("| previousBlock: %d\n", (this.previousBlock == null) ? null : this.previousBlock.getBlockNumber() );
        block += String.format("| hash: %s\n", this.hash);
        block += separator;

        for (Transaction transaction : transactionList) {
            block += String.format("| %s\n", transaction);
        }

        block += separator;

        for( Map.Entry<String, Account> entry: this.accountBalanceMap.entrySet() ) {
            block += String.format("| %s\n", entry.getValue());
        }

        block += separator;
        return block;
    }

    /**
     *
     * @return
     */
    public String validate() {
        if ( ! validateTransactions() ) {
            return "Invalid block. The block does not contain exactly 10 transactions.";
        }

        String accountBalanceStatus;
        if ( (accountBalanceStatus = validateAccountBalances()) != null ) {
            return accountBalanceStatus;
        }

        if ( ! validateHash() ) {
            return "Invalid block. Hashes do not match.";
        }


        return null;
    }

    private Boolean validateTransactions() {
        return transactionList.size() == 10;
    }

    private String validateAccountBalances() {
        Integer totalCurrency = 0;
        // Iterate through accounts to retrieve their current balances.
        for (Map.Entry<String, Account> entry : this.accountBalanceMap.entrySet() ) {
            int balance = entry.getValue().getBalance();

            if (balance < MIN_ACCOUNT_BALANCE || balance > MAX_ACCOUNT_BALANCE) {
//                System.err.println("Account does not have acceptable balance.");
                return "Invalid block. Account does not have acceptable balance.";
//                return false;
            }

            totalCurrency += entry.getValue().getBalance();
        }

        if (totalCurrency != MAX_ACCOUNT_BALANCE) {
//            System.err.println("Total currency does not equal MAX_ACCOUNT_BALANCE.");
            return "Invalid block. Total currency does not equal MAX_ACCOUNT_BALANCE.";
            //            return false;
        }

        return null;
//        return true;
    }

    private Boolean validateHash() {
        if (this.previousBlock != null) {
            String checkHash = computeHash(this.previousBlock);

            System.out.println("previousHash: " + this.previousHash);
            System.out.println("   checkHash: " + checkHash);


            if (! checkHash.equals(this.previousHash)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Citations:
     * @source https://dev.to/monknomo/make-an-immutable-object---in-java-480n
     */
    public void commitBlock(String seed) {

        this.transactionList = Collections.unmodifiableList(this.transactionList);

        Map<String, Account> immutableBalanceMap = new HashMap<String, Account>();
        immutableBalanceMap.putAll(this.accountBalanceMap);
        this.accountBalanceMap = Collections.unmodifiableMap(immutableBalanceMap);

        this.hash = computeHash(this, seed);
        return;
    }


    public Integer getBlockNumber() {
        return this.blockNumber;
    }

    public void setPreviousBlock(Block block) {
        this.previousBlock = block;
    }


    private ByteArrayOutputStream serializeObject(Object object) {
//        System.out.println("IN SERIALIZE, object is " + object);
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


    private byte[] serializeToArray(Object object) {
        //        System.out.println("IN SERIALIZE, object is " + object);
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
        return bos.toByteArray();
    }

    /**
     * Compute hash of block.
     *
     * Citations:
     * with help from StackOverflow
     * https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
     * https://www.baeldung.com/sha-256-hashing-java
     *
     * Creating a byte array:
     * https://stackoverflow.com/questions/5368704/appending-a-byte-to-the-end-of-another-byte/5368731
     * @return
     */

    private String computeHash(Block blockToHash, String seed) {
        // If no block to hash, ignore (the genesis block)
        if (blockToHash == null ) {
            return null;
        }

        // Convert Block into a byte[]
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
            block.write(serializeToArray(blockToHash.getTransactionList()));
            block.write(serializeToArray(blockToHash.getBalanceMap()));
        } catch (IOException e) {
            System.out.println(e);
        }


        byte [] toHash = block.toByteArray();

        // Initialize a SHA-256 hasher
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Oops, NoSuchAlgorithmException caught");
        }

        // Hash the byte-ified Block object
        byte[] encodedHash = digest.digest(toHash);

        // Converting byte[] to String
        // src: https://howtodoinjava.com/array/convert-byte-array-string-vice-versa/
        return Base64.getEncoder().encodeToString(encodedHash);
    }
}