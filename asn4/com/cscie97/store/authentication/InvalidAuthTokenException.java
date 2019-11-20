package com.cscie97.store.authentication;

/**
 * Returns from AuthenticationService methods when a token fails to validate.
 * The Exception is thrown whenever the "active" property on the AuthToken is
 * set to False, and can be due to timeout or the User logging out of the system.
 *
 * Captures the invalid AuthToken id and the reason for failure.
 *
 * @author Matthew Thomas
 */
public class InvalidAuthTokenException extends Exception {
    private String authTokenId;
    private String reason;

    /** Constructor for an individual command exception. */
    public InvalidAuthTokenException(String authTokenId, String reason) {
        super(authTokenId);
        this.authTokenId = authTokenId;
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
        return "invalid token " + this.authTokenId + ": " + this.reason;
    }
}