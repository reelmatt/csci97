package com.cscie97.ledger;

import java.util.Map;
import java.util.HashMap;
import java.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
    // Properties
    private Integer blockNumber;
    private String previousHash;
    private String hash;

    // Associations
    private Transaction transactionList;
    private Map<String, Account> accountBalanceMap;
    private Block previousBlock;

    public Block (int number) {
        this.blockNumber = number;
        this.accountBalanceMap = new HashMap<String, Account>();

        Account master = new Account("master", Integer.MAX_VALUE);

        this.accountBalanceMap.put("master", master);
        this.hash = computeHash();
    }

    public Block (int number, String previousHash) {
        this.blockNumber = number;
        this.previousHash = previousHash;
        this.hash = computeHash();
    }


    public Account getAccount(String address) {
        return this.accountBalanceMap.get("master");
    }

    public String toString() {
        return "Block " + this.blockNumber + " has a hash of " + this.hash;
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