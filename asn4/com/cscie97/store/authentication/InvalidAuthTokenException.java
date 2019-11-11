package com.cscie97.store.authentication;

/**
 * @author Matthew Thomas
 */
public class InvalidAuthTokenException extends Exception {
    private String action;
    private String reason;

    /** Constructor for an individual command exception. */
    public InvalidAuthTokenException(String action, String reason) {
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