package com.cscie97.ledger;

import java.util.Map;
import java.util.HashMap;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.Serializable;
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
    public String previousHash;

    /** The hash of the current block, based on all properties and associations. */
    public String hash;

    /** An ordered list of Transactions that are included in the current block. */
    private List<Transaction> transactionList;

    /** Full set of accounts managed by the Ledger. */
    private Map<String, Account> accountBalanceMap;

    /** References the preceding Block in the blockchain. */
    private Block previousBlock;

    /**
     * Block Constructor
     *
     * @param number        Block number (ID)
     * @param previousBlock Contents of previousBlock (for hashing)
     */
    public Block (int number, Block previousBlock) {
        this.blockNumber = number;
        this.previousHash = (previousBlock != null) ? previousBlock.previousHash : null;

        this.transactionList = new ArrayList<Transaction>();

        if (previousBlock != null) {
            this.accountBalanceMap = previousBlock.getBalanceMap();
        } else {
            this.accountBalanceMap = new HashMap<String, Account>();
        }

        this.previousBlock = previousBlock;
    }

    /**
     * @return
     */
    public Map<String, Account> getBalanceMap() {
        return this.accountBalanceMap;
    }

    /**
     * @param account The account to add to the current balance map.
     */
    public void addAccount(Account account) {
        this.accountBalanceMap.put(account.getAddress(), account);
        return;
    }

    /**
     * Setter
     */
    public void clearTransactions() {
        this.transactionList = new ArrayList<Transaction>();
        this.blockNumber++;
    }

    /**
     * Getter
     * @param address
     * @return
     */
    public Account getAccount(String address) {
        return this.accountBalanceMap.get(address);
    }

    /**
     * Setter
     * @param transaction
     * @return
     */
    public int addTransaction(Transaction transaction) {
        // Add valid transaction to list
        this.transactionList.add(transaction);

//        System.out.println("BLOCK: added transaction " + transaction.getTransactionId() + " to block " + this.blockNumber);
        // Check if we've reached 10 transactions for the block
        if (this.transactionList.size() == 10 ) {
//            System.out.println("BLOCK: we've reached 10 transactions");
            return 10;
        }

        return this.transactionList.size();
    }

    public Transaction getTransaction(String transactionId) {
//        System.out.println("BLOCK: in getTransaction()");
        for (Transaction transaction : transactionList) {
//            System.out.print("BLOCK: transaction = " + transaction.getTransactionId());
//            System.out.print("\ttransactionId = " + transactionId + "\n");
            if ( transactionId.equals(transaction.getTransactionId()) ) {
//                System.out.println("BLOCK: transaction found...");
                return transaction;
            }
        }
//        System.out.println("BLOCK: NO TRANSACTION FOUND.");
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
     * Compute hash of block.
     *
     * Citations:
     * with help from StackOverflow
     * https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
     * https://www.baeldung.com/sha-256-hashing-java
     *
     * @return
     */

    private String computeHash() {
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Oops, NoSuchAlgorithmException caught");
        }

        String properties = this.previousHash + this.hash;
        byte[] encodedHash = digest.digest(properties.getBytes());

        // Converting byte[] to String
        // https://howtodoinjava.com/array/convert-byte-array-string-vice-versa/
        System.out.println("in computeHash, printing byte[] results in " + encodedHash);
        String s = Base64.getEncoder().encodeToString(encodedHash);
        System.out.println("in computeHash, saved new String () is  " + s);
        return s;
    }
}