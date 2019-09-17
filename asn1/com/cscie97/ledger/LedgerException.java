package com.cscie97.ledger;

/**
 * Returns from the Ledger API methods in response to an error condition.
 *
 * Captures the action that was attempted and the reason for failure.
 *
 * @author Matthew Thomas
 */
public class LedgerException extends Exception {
    private String action;
    private String reason;

    public LedgerException(String action, String reason) {
        super(action);
        this.action = action;
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }
    public String toString() {
        return this.action + ": " + this.reason;
    }
}