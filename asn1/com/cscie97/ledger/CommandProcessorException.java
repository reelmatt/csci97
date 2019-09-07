package com.cscie97.ledger;

public class CommandProcessorException extends Exception {
    private String command;
    private String reason;
    private int lineNumber;

    public CommandProcessorException() {
        super();
    }

    public CommandProcessorException(String message, String reason, int lineNumber) {
        super(message);
        this.command = message;
        this.reason = reason;
        this.lineNumber = lineNumber;

        System.out.println("command: " + this.command);
        System.out.println("reason: " + this.reason);
        System.out.println("line number: " + this.lineNumber);

    }

}