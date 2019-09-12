package com.cscie97.ledger;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * CommandProcessor - Utility class to feed Ledger a set of operations.
 *
 * Citations:
 *
 * Regex pattern and matching found in this StackOverflow post
 * https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double?rq=1
 */
public class CommandProcessor {
    /** Ledger to perform actions on. */
    private Ledger ledger = null;

    /**
     * Process a single command.
     *
     * Output of the command is formatted and displayed to stdout.
     *
     * @param commandLine
     * @throws CommandProcessorException
     */
    public void processCommand(String commandLine) throws CommandProcessorException {

        List<String> args = parseCommand(commandLine);

//        System.out.println("\targs IN COMMAND: " + args.toString());
        String command = args.remove(0);
        switch (command.toLowerCase()) {
            case "create-ledger":
                createLedger(args);
                break;
            case "create-account":
                createAccount(args);
                break;
            case "get-account-balance":
            case "get-account-balances":
                getAccountBalance(args);
                break;
            case "get-block":
                getBlock(args);
                break;
            case "process-transaction":
                processTransaction(args);
                break;
            default:
                System.out.println("Other command is " + command);
                break;
        }
        return;
    }

    /**
     * Process a set of commands provided within the given 'commandFile'.
     *
     * @param file
     * @throws CommandProcessorException
     */
    public void processCommandFile(String commandFile) throws CommandProcessorException {
        String currentLine = "";
        Integer currentLineNumber = 0;

        try {
            FileReader testScript = new FileReader(commandFile);
            BufferedReader reader = new BufferedReader(testScript);

            while ( (currentLine = reader.readLine()) != null ) {
                currentLineNumber++;

                if (currentLine.length() > 0 && currentLine.charAt(0) != '#') {
                     processCommand(currentLine);
                } else {
                    System.out.println();
                }
            }

            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            System.err.println(e);
            throw new CommandProcessorException(currentLine, "file not found", currentLineNumber);
        } catch (IOException e) {
            System.err.println("oops, IO exception");
            System.err.println(e);
            throw new CommandProcessorException(currentLine, "IO exception", currentLineNumber);
        } catch (CommandProcessorException e) {
            System.err.println(e.toString());
        }
    }

    private static List<String> parseCommand(String command) {
        List<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"");
        Matcher regexMatcher = regex.matcher(command);

        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                matchList.add(regexMatcher.group(1));
            } else {
                matchList.add(regexMatcher.group());
            }

        }

        return matchList;
    }

    private void createAccount(List<String> args) {

        if (this.ledger != null) {
            try {
                Account account = this.ledger.createAccount(args.get(0));
                System.out.println("Created account '" + account.getId() + "'.");
            } catch (LedgerException e) {
                System.err.println(e.toString());
            }
        }
        return;
    }

    private void createLedger(List<String> args) {
        try {
            this.ledger = new Ledger(args.get(0), args.get(1), args.get(2));
        } catch (LedgerException e) {
            System.err.println(e.toString());
        }

        System.out.println("Created ledger '" + args.get(0) + "'.");
        return;
    }

    private void processTransaction(List<String> args) {
        String id = null;
        String payload;
        Account payer, receiver;

        try {
            // If the transaction is null, or doesn't exist, allowed to make a new one
            if (this.ledger.getTransaction(args.get(0)) == null) {
                id = args.get(0);
            } else {
                throw new LedgerException("process transaction", "Transaction already exists.");
            }

            payer = this.ledger.getAccount(args.get(8));
            receiver = this.ledger.getAccount(args.get(10));
        } catch (LedgerException e) {
            System.err.println(e.toString());
            return;
        }

        // Create the transaction
        Transaction newTransaction = new Transaction(
                id,                             // id
                Integer.parseInt(args.get(2)),  // amount
                Integer.parseInt(args.get(4)),  // fee
                args.get(6),                    // payload
                payer,                          // payer Account
                receiver                        // receiver's Account
        );

        // Try to process it, adding to in-progress Block
        try {
            String transactionStatus = this.ledger.processTransaction(newTransaction);
            System.out.println("Processed transaction #" + transactionStatus);
        } catch (LedgerException e) {
            System.err.println(e.toString());
        }


        return;
    }

    private void getAccountBalance(List<String> args) {
        try {
            // No arguments, get all balances
            if (args.size() == 0) {
                this.ledger.getAccountBalances();
            } else {
                System.out.println(args.get(0) + " has an account balance of " + this.ledger.getAccountBalance(args.get(0)));
            }
        } catch (LedgerException e) {
            System.err.println(e.toString());
        }

        return;
    }

    private void getBlock(List<String> args) throws CommandProcessorException {
        Block block;

        if ( (block = this.ledger.getBlock(Integer.parseInt(args.get(0)))) == null ) {
            throw new CommandProcessorException("get block", "Block " + args.get(0) + " does not exist.");
        }
//        try {
//            block = this.ledger.getBlock(Integer.parseInt(args.get(0)));
//        } catch (LedgerException e) {
//
//        }
        System.out.print(block);
//        System.out.println("Retrieved block #" + args.get(0));
        return;
    }

    private void getTransaction(List<String> args) throws CommandProcessorException {
        Transaction transaction;

        try {
            if ( (transaction = this.ledger.getTransaction(args.get(0))) == null ) {
                throw new CommandProcessorException();
            }

            System.out.println("Retrieved transaction #" + args.get(0));
        } catch (LedgerException e) {

        }

    }

    private void validate() {

    }
}