package com.cscie97.store.model;

/**
 * Returns from the Ledger API methods in response to an error condition.
 *
 * Captures the action that was attempted and the reason for failure.
 *
 * @author Matthew Thomas
 */
public class StoreModelServiceException extends Exception {
    private String action;
    private String reason;

    /** Constructor for an individual command exception. */
    public StoreModelServiceException(String action, String reason) {
        super(action);
        this.action = action;
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    /** Overrides default toString() method. */
    public String toString() {
        return this.action + ": " + this.reason;
    }
}