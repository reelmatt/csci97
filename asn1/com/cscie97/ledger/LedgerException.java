package com.cscie97.ledger;

public class LedgerException extends Exception {
    private String action;
    private String reason;

    public LedgerException() {
        super();
    }

    public LedgerException(String message, String reason) {
        super(message);
        this.action = message;
        this.reason = reason;
        System.out.println("LedgerException....");
    }
}