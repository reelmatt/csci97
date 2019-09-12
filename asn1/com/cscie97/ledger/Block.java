package com.cscie97.ledger;

import java.util.Map;
import java.util.HashMap;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Block - Aggregates groups of transactions in the blockchain.
 *
 * @author Matthew Thomas
 */
public class Block {
    // Properties
    private Integer blockNumber;
    public String previousHash;
    public String hash;

    // Associations
    private List<Transaction> transactionList;
    private Map<String, Account> accountBalanceMap;
    private Block previousBlock;


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
        //
//        Account master = new Account("master");
//
//        this.accountBalanceMap.put("master", master);
    }

    public Map<String, Account> getBalanceMap() {
        return this.accountBalanceMap;
    }

    public void addAccount(Account account) {
//        System.out.println("BLOCK: addAccount()" + account.getId());
        this.accountBalanceMap.put(account.getId(), account);
        return;
    }

    public Account getAccount(String address) {
//        System.out.println("BLOCK: get account " + address);
//        System.out.println("Map size() is " + this.accountBalanceMap.size());
        return this.accountBalanceMap.get(address);
    }

    public int addValidTransaction(Transaction transaction) {
        // Add valid transaction to list
        this.transactionList.add(transaction);

//        System.out.println("BLOCK: added transaction " + transaction.getTransactionId() + " to block " + this.blockNumber);
        // Check if we've reached 10 transactions for the block
        if (this.transactionList.size() == 10 ) {
            System.out.println("BLOCK: we've reached 10 transactions");
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

    public String toString() {
        String block = "---------------------------\n";
        block +=       "Block #" + this.blockNumber + "\n";
        block +=       "previousHash: " + this.previousHash + "\n";
        block +=       "hash: " + this.hash + "\n";
        block +=       "---------------------------\n";

        for (Transaction transaction : transactionList) {
            block += "Transaction #" + transaction.getTransactionId() + "\n";
        }

        for( Map.Entry<String, Account> entry: this.accountBalanceMap.entrySet() ) {
            block += "Account " + entry.getKey() + " balance: " + entry.getValue().getBalance() + "\n";
        }

        block +=       "---------------------------\n";
        return block;
//        return "Block " + this.blockNumber + " has a hash of " + this.hash;
    }

    // with help from StackOverflow
    // https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
    // https://www.baeldung.com/sha-256-hashing-java
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