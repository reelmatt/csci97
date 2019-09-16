package com.cscie97.ledger;

public class CommandProcessorException extends Exception {
    private String command;
    private String reason;
    private int lineNumber = -1;

    public CommandProcessorException() {
        super();
    }

    public CommandProcessorException(String command, String reason) {
        super(command);
        this.command = command;
        this.reason = reason;
    }

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
        String error = this.command + ": " + this.reason;

        if (this.lineNumber != -1) {
            String line = " (line " + this.lineNumber + ").";
            error += line;
        }
        return error;
    }
}