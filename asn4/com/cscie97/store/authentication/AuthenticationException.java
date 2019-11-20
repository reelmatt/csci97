package com.cscie97.store.authentication;

/**
 * Returns from AuthenticationService methods in response to error conditions.
 *
 * Captures the command that was attempted and the reason for failure.
 *
 * @author Matthew Thomas
 */
public class AuthenticationException extends Exception {
    private String action;
    private String reason;

    /** Constructor for an individual command exception. */
    public AuthenticationException(String action, String reason) {
        super(action);
        this.action = action;
        this.reason = reason;
    }

    /**
     * Returns the reason the token was invalid.
     */
    public String getReason() {
        return this.reason;
    }

    /** Overrides default toString() method. */
    public String toString() {
        return this.action + ": " + this.reason;
    }
}