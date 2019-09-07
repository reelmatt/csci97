package com.cscie97.ledger;

public class CommandProcessorException extends Exception {
    private String command;
    private String reason;
    private int lineNumber;

    public String CommandProcessorException(String command, String reason, Integer lineNumber) {
        command = command;
        reason = reason;
        lineNumber = lineNumber;

        System.out.println("command: " + command);
        System.out.println("reason: " + reason);
        System.out.println("line number: " + lineNumber);

        return command;
    }

}