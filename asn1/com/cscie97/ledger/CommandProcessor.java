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
                createLedger(command, args);
                break;
            case "create-account":
                createAccount(command, args);
                break;
            case "process-transaction":
                processTransaction(command, args);
                break;
            case "get-account-balance":
            case "get-account-balances":
                getAccountBalance(command, args);
                break;
            case "get-block":
                getBlock(command, args);
                break;
            case "get-transaction":
                getTransaction(command, args);
                break;
            case "validate":
                validate(command);
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
        BufferedReader reader;
        String currentLine = "";
        Integer currentLineNumber = 0;
        Boolean printBlank = true;

        // Open file 'commandFile'
        try {
            reader = new BufferedReader(new FileReader(commandFile));
        } catch (FileNotFoundException e) {
            throw new CommandProcessorException(currentLine, e.toString(), currentLineNumber);
        }

        // Read file
        try {
            while ( (currentLine = reader.readLine()) != null ) {
                currentLineNumber++;

                // Skip blank lines, or comments (indicated by '#')
                if (currentLine.length() <= 0) {
                    // Print one blank line in stdout for group of blanks/comments
                    if (printBlank) {
                        System.out.println();
                        printBlank = false;
                    }
                } else if (currentLine.charAt(0) == '#') {
                    System.out.println(currentLine);
                } else if (currentLine.length() > 0) {
                    try {
                        processCommand(currentLine);
                        printBlank = true;
                    } catch (CommandProcessorException e) {
                        System.err.println(e);
//                        throw new CommandProcessorException(e.getCommand(), e.getReason(), currentLineNumber);
                    }
                }
            }
            reader.close();

        } catch (IOException e) {
            throw new CommandProcessorException(currentLine, e.toString(), currentLineNumber);
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
    private void createAccount(String command, List<String> args) throws CommandProcessorException {
        // Error if no Ledger
        if (this.ledger == null) {
            throw new CommandProcessorException(command, "Ledger has not been initialized.");
        }

        // Try to create new account
        try {
            Account account = this.ledger.createAccount(args.get(0));
            System.out.println("Created account '" + account.getAddress() + "'");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing 'account-id'");
        } catch (LedgerException e) {
            System.err.println(e);
        }
        return;
    }

    /**
     * Create a new ledger with the given name, description, and seed value.
     *
     * Expected command line to be formatted as:
     *      create-ledger <name> description <description> seed <seed>
     *
     * @param   args                        Command line arguments.
     * @throws  CommandProcessorException   If command line arguments are missing, indicated by
     *                                      an IndexOutOfBoundsException.
     */
    private void createLedger(String command, List<String> args) throws CommandProcessorException {
        if (this.ledger != null) {
            throw new CommandProcessorException(command, "A Ledger has already been initialized.");
        }

        // Look for arguments and create ledger
        try {
            String name = args.get(0);
            String description = (String) getArgument("description", args);
            String seed = (String) getArgument("seed", args);

            this.ledger = new Ledger(name, description, seed);
            System.out.println(String.format("Created ledger %s", this.ledger));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (LedgerException e) {
            System.err.println(e);
        }

        return;
    }

    /**
     * Looks for argument following the specified 'key' in the command line.
     *
     * It is assumed the appropriate argument follows the specified 'key' in the command line,
     * and therefore is retrieved by accessing index + 1.
     *
     * @param   key                         The argument to look for.
     * @param   args                        The command line to search.
     * @return  Object                      The Object located at (index + 1) of the 'key'.
     * @throws  IndexOutOfBoundsException   If command line arguments are missing.
     */
    private Object getArgument(String key, List<String> args) throws IndexOutOfBoundsException {
        int index;
        Object value = null;

        if ((index = args.indexOf(key)) != -1) {
            return value = args.get(index + 1);
        }

        return null;
    }

    /**
     * Process a new transaction.
     *
     * Expected command line to be formatted as:
     *      process-transaction <transaction-id>
     *                          amount <amount>
     *                          fee <fee>
     *                          payload <payload>
     *                          payer <account-address>
     *                          receiver <account-address>
     *
     * @TODO Need more argument error checking.
     *
     * @param args Command line arguments.
     *
     * @see getArgument()
     */
    private void processTransaction(String command, List<String> args) throws CommandProcessorException {
        try {
            // Extract arguments from command line
            String transactionId = args.get(0);
            Integer amount = Integer.parseInt((String) getArgument("amount", args));
            Integer fee = Integer.parseInt((String) getArgument("fee", args));
            String payload = (String) getArgument("payload", args);
            String payerAddress = (String) getArgument("payer", args);
            String receiverAddress = (String) getArgument("receiver", args);

            // Check transaction ID and accounts are valid
            String id = this.ledger.validateTransactionId(transactionId);
            Account payer = this.ledger.validateAccount(payerAddress);
            Account receiver = this.ledger.validateAccount(receiverAddress);

            // Create the transaction
            Transaction newTransaction = new Transaction(id, amount, fee, payload, payer, receiver);

            // Output transaction status
            String transactionStatus = this.ledger.processTransaction(newTransaction);
            System.out.println("Processed transaction #" + transactionStatus);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, e.toString());
        } catch (LedgerException e) {
            System.err.println(e);
            return;
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
    private void getAccountBalance(String command, List<String> args) {
        // Init a new map to store 1 account balance, or an entire map
        Map<String, Integer> balances = new HashMap<String, Integer>();

        try {
            // No arguments, get all balances
            if (args.size() == 0) {
                balances = this.ledger.getAccountBalances();
            } else {
                balances.put(args.get(0), this.ledger.getAccountBalance(args.get(0)));
            }
        } catch (LedgerException e) {
            System.err.println(e);
        }

        // Output the map to stdout
        if (balances != null) {
            for (Map.Entry<String, Integer> account : balances.entrySet()) {
                System.out.println(String.format("Account %s: current balance = %d", account.getKey(), account.getValue()));
            }
        }

        return;
    }

    /**
     * Output the details for the given block number.
     *
     * @param   args                        Command line arguments.
     * @throws  CommandProcessorException   Block does not exist.
     */
    private void getBlock(String command, List<String> args) throws CommandProcessorException {
        try {
            Integer blockNumber = Integer.parseInt(args.get(0));
            Block block = this.ledger.getBlock(blockNumber);
            System.out.print(block);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments");
        } catch (LedgerException e) {
            throw new CommandProcessorException(command, e.getReason());
        }

        return;
    }

    /**
     * Output the details of the given transaction id.
     *
     * @param   args                        Command line arguments.
     * @throws  CommandProcessorException   Transaction not found in the Ledger.
     */
    private void getTransaction(String command, List<String> args) throws CommandProcessorException {

        try {
            String transactionId = args.get(0);
            Transaction transaction = this.ledger.getTransaction(transactionId);
            System.out.println("Retrieved transaction: " + transaction);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments");
        } catch (LedgerException e) {
            throw new CommandProcessorException(command, e.getReason());
        }

        return;
    }

    /**
     * Validate the current state of the block chain.
     */
    private void validate(String command) {
        try {
            this.ledger.validate();
        } catch (LedgerException e) {
            System.err.println(e);
        }

        System.out.println("Blockchain successfully validated.");

        return;
    }
}