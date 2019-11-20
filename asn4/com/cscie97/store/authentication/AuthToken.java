package com.cscie97.store.authentication;

import java.util.Date;

/**
 * An AuthToken to validate a User's permissions.
 *
 * @author Matthew Thomas
 */
public class AuthToken {
    /** The AuthToken's unique ID. */
    private String id;

    /** Time the AuthToken was issued. */
    private Date timeIssued;

    /** The state of the AuthToken. True if valid, false otherwise. */
    private boolean active;

    /**
     * AuthToken Constructor.
     *
     * Creates a new AuthToken with the provided ID. The token is associated
     * with a User and managed by the Authentication Service.
     *
     * @param id    The unique ID of the AuthToken.
     */
    public AuthToken(String id) {
        this.id = id;
        this.active = true;

        // Get current time
        this.timeIssued = new Date();
    }

    /**
     * Invalidates the AuthToken to prevent further use.
     */
    public void invalidate() {
        this.active = false;
    }

    /**
     * Returns the current state of the AuthToken, true if valid, false if
     * invalid.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Returns the AuthToken id.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the time the AuthToken was issued.
     */
    public long getTimeIssued() {
        return this.timeIssued.getTime();
    }
    /**
     * Override default toString method.
     */
    public String toString() {
        return "token" + id;
    }
}