package com.cscie97.store.authentication;

/**
 * Returns from AuthenticationService methods when the User requesting access
 * does not have the required Permissions to make a public API call.
 *
 * Captures the command that was attempted and the reason for failure.
 *
 * @author Matthew Thomas
 */
public class AccessDeniedException extends Exception {
    private String action;
    private String permission;

    /** Constructor for an individual command exception. */
    public AccessDeniedException(String action, String permission) {
        super(action);
        this.action = action;
        this.permission = permission;
    }

    /**
     * Returns the permission needed to complete the API call.
     */
    public String getReason() {
        return this.permission;
    }

    /** Overrides default toString() method. */
    public String toString() {
        return this.action + ": does not have permission '" + this.permission + "'";
    }
}