package com.cscie97.ledger;

public class Block {
    private blockNumber;
    private previousHash;
    private hash;

    public Block (int number) {
        this.blockNumber = number;

    }

    public Block (int number, int previousHash) {
        this.blockNumber = number;
        this.previousHash = previousHash;
    }

}