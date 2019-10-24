package com.cscie97.store.controller;

/**
 * Returns from CommandProcessor methods in response to error conditions.
 *
 * Captures the command that was attempted and the reason for failure. In the
 * case where commands are read from a file, the line number of the command
 * should be inlcuded in the exception.
 *
 * @author Matthew Thomas
 */
public class CommandProcessorException extends Exception {
    private String command;
    private String reason;
    private int lineNumber = -1;

    /** Constructor for an individual command exception. */
    public CommandProcessorException(String command, String reason) {
        super(command);
        this.command = command;
        this.reason = reason;
    }

    /** Constructor for a command exception read from a file. */
    public CommandProcessorException(String command, String reason, int lineNumber) {
        super(command);
        this.command = command;
        this.reason = reason;
        this.lineNumber = lineNumber;
    }

    public String getCommand() {
        return this.command;
    }

    public String getReason() {
        return this.reason;
    }

    /** Overrides default toString() method. */
    public String toString() {
        String error = String.format("%s: %s", this.command, this.reason);

        // If there's a line number, include it in the output
        if (this.lineNumber != -1) {
            error += String.format(" (line %d).", this.lineNumber);
        }
        return error + "\n";
    }
}