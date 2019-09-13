package com.cscie97.ledger;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.Map;
import java.util.HashMap;

/**
 * CommandProcessor - Utility class to feed Ledger a set of operations.
 *
 * @author Matthew Thomas
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
        // Break command line into a list of arguments
        List<String> args = parseCommand(commandLine);

        // First argument is the command to run
        String command = args.remove(0);

        // Pass remaining args into helper methods
        switch (command.toLowerCase()) {
            case "create-ledger":
                createLedger(args);
                break;
            case "create-account":
                createAccount(args);
                break;
            case "process-transaction":
                processTransaction(args);
                break;
            case "get-account-balance":
            case "get-account-balances":
                getAccountBalance(args);
                break;
            case "get-block":
                getBlock(args);
                break;
            case "get-transaction":
                getTransaction(args);
                break;
            default:
                throw new CommandProcessorException(command, "Unknown command");
        }

        return;
    }

    /**
     * Process a set of commands provided within the given 'commandFile'.
     *
     * @param   file
     * @throws  CommandProcessorException   Error conditions.
     */
    public void processCommandFile(String commandFile) throws CommandProcessorException {
        // Keep track of current line, and line number, in file
        String currentLine = "";
        Integer currentLineNumber = 0;
        Boolean printBlank = true;

        try {
            // Open file 'commandFile'
            FileReader testScript = new FileReader(commandFile);
            BufferedReader reader = new BufferedReader(testScript);

            // Read each line
            while ( (currentLine = reader.readLine()) != null ) {
                currentLineNumber++;

                // Skip blank lines, or comments (indicated by '#')
                if (currentLine.length() > 0 && currentLine.charAt(0) != '#') {
                     processCommand(currentLine);
                     printBlank = true;
                } else {
                    // Print one blank line in stdout for group of blanks/comments
                    if (printBlank) {
                        System.out.println();
                        printBlank = false;
                    }
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
            System.err.println(e);
        }

        return;
    }

    /**
     * Parses command line by whitespace, keeping quoted arguments intact.
     *
     * Citations:
     * Regex pattern and matching found in this StackOverflow post
     * https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double?rq=1
     *
     * @param   commandLine     The command line to parse.
     * @return                  List of arguments, split by whitespace.
     */
    private static List<String> parseCommand(String commandLine) {
        List<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"");
        Matcher regexMatcher = regex.matcher(commandLine);

        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                matchList.add(regexMatcher.group(1));
            } else {
                matchList.add(regexMatcher.group());
            }
        }

        return matchList;
    }

    /**
     * Create a new account with the given account id.
     *
     * Expected command line to be formatted as:
     *      create-account <account-id>
     *
     * Additional arguments provided are ignored.
     *
     * @param   args                        Command line arguments.
     * @throws  CommandProcessorException   If Ledger has not been initialized.
     * @throws  IndexOutOfBoundsException   No argument provided on command line.
     */
    private void createAccount(List<String> args) throws CommandProcessorException {
        // Error if no Ledger
        if (this.ledger == null) {
            throw new CommandProcessorException(
                "create account",
                "Ledger has not been initialized."
            );
        }

        // Try to create new account
        try {
            Account account = this.ledger.createAccount(args.get(0));
            System.out.println("Created account '" + account.getAddress() + "'");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException("create account", "Missing 'account-id'");
        } catch (LedgerException e) {
            System.err.println(e);
        }
        return;
    }

    /**
     * Create a new ledger with the given name, description, and seed value.
     * @param args Command line arguments.
     */
    private void createLedger(List<String> args) throws CommandProcessorException {
        try {
            this.ledger = new Ledger(args.get(0), args.get(1), args.get(2));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException("create ledger", "Missing arguments.");
        } catch (LedgerException e) {
            System.err.println(e);
        }

        System.out.println(String.format("Created ledger '%s'", this.ledger.getName()));
        return;
    }

    /**
     * Process a new transaction.
     *
     * @TODO Need more argument error checking.
     *
     * @param args Command line arguments.
     */
    private void processTransaction(List<String> args) throws CommandProcessorException {
        String id = args.get(0);
        String payload = args.get(6);
        int amount = Integer.parseInt(args.get(2));
        int fee = Integer.parseInt(args.get(4));
        Account payer = null;
        Account receiver = null;

        // If validation == true, ID is already in use. Throw exception.
        if ( this.ledger.validateTransactionId(id) ) {
            throw new CommandProcessorException("process transaction", "Transaction ID already used.");
        }

        // retrieve accounts
        try {
            payer = this.ledger.getAccount(args.get(8));
            receiver = this.ledger.getAccount(args.get(10));
        } catch (LedgerException ex) {
            System.err.println(ex);
            return;
        }

        // Create the transaction
        Transaction newTransaction = new Transaction(id, amount, fee, payload, payer, receiver);

        // Try to process it, adding to in-progress Block
        try {
            String transactionStatus = this.ledger.processTransaction(newTransaction);
            System.out.println("Processed transaction #" + transactionStatus);
        } catch (LedgerException e) {
            System.err.println(e);
        }

        return;
    }

    /**
     * Output account balances.
     *
     * Handles output of individual accounts, specified by an accountId. For
     * 'get-account-balances' command, it outputs all account balances for the
     * most recent completed block.
     *
     * @param args Command line arguments.
     */
    private void getAccountBalance(List<String> args) {
        try {
            // No arguments, get all balances
            if (args.size() == 0) {
                Map<String, Integer> balances = this.ledger.getAccountBalances();
                System.out.println("Retrieve account balances for " + balances.size() + " accounts.");
            } else {
                System.out.println(args.get(0) + " has an account balance of " + this.ledger.getAccountBalance(args.get(0)));
            }
        } catch (LedgerException e) {
            System.err.println(e.toString());
        }

        return;
    }

    /**
     * Output the details for the given block number.
     *
     * @param   args                        Command line arguments.
     * @throws  CommandProcessorException   Block does not exist.
     */
    private void getBlock(List<String> args) throws CommandProcessorException {
        Block block;

        if ( (block = this.ledger.getBlock(Integer.parseInt(args.get(0)))) == null ) {
            throw new CommandProcessorException("get block", "Block " + args.get(0) + " does not exist.");
        }

        System.out.print(block);
        return;
    }

    /**
     * Output the details of the given transaction id.
     *
     * @param   args                        Command line arguments.
     * @throws  CommandProcessorException   Transaction not found in the Ledger.
     */
    private void getTransaction(List<String> args) throws CommandProcessorException {
        Transaction transaction;

        try {
            if ( (transaction = this.ledger.getTransaction(args.get(0))) == null ) {
                throw new CommandProcessorException();
            }

            System.out.println("Retrieved transaction #" + args.get(0));
        } catch (LedgerException e) {

        }

        return;
    }

    /**
     * Validate the current state of the block chain.
     */
    private void validate() {

    }
}