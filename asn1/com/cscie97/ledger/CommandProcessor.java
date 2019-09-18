package com.cscie97.ledger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
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
 * This class is used by the TestDriver to open a file containing a set of
 * commands. It then processes each command, outputting a formatted response,
 * or the error message from the caught Exception. It contains several helper
 * methods to process a command for each publically-accessible API of the Ledger
 * Service.
 *
 * @author Matthew Thomas
 */
public class CommandProcessor {
    /** Ledger to perform actions on. */
    private Ledger ledger = null;

    /**
     * Process a set of commands provided within the given 'commandFile'.
     *
     * Creates a buffer to read the specified command file line-by-line. All
     * input lines will also be output to stdout, including comments and blank
     * lines, as suggested in post @67 on Piazza. Any non-comment or non-blank
     * line will be sent to processCommand() where it will be parsed into
     * arguments and checked against a list of public API methods provided by
     * the Ledger.
     *
     * @param   commandFile                 Name of the file to open/process.
     * @throws  CommandProcessorException   When FileNotFound or IO exceptions
     *                                      come up, or if processCommand does
     *                                      not recognize the command line.
     */
    public void processCommandFile(String commandFile) throws CommandProcessorException {
        // Keep track of current line and line number
        String currentLine = "";
        Integer currentLineNumber = 0;

        // Open file 'commandFile' into a buffer
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(commandFile));
        } catch (FileNotFoundException e) {
            throw new CommandProcessorException("open file", e.toString(), currentLineNumber);
        }

        // Read file
        try {
            while ( (currentLine = reader.readLine()) != null ) {
                currentLineNumber++;

                // Print the input line to stdout - see Piazza post @67
                System.out.println(currentLine);

                // Skip blank lines, or comments (indicated by a starting '#')
                if (currentLine.length() > 0 && currentLine.charAt(0) != '#') {
                    try {
                        processCommand(currentLine, currentLineNumber);
                    } catch (CommandProcessorException e) {
                        // catch individual command exceptions to continue reading file
                        System.err.println(e);
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
     * Process a single command.
     *
     * Output of the command is formatted and displayed to stdout. Any
     * LedgerExceptions are caught with their contents output to stderr.
     * Problems with the commands written from the file, missing arguments, or
     * others, will throw a CommandProcessorException.
     *
     * @param   commandLine                 The current command line from the
     *                                      input file.
     * @param   lineNumber                  The current line number in the file.
     * @throws  CommandProcessorException   If there is a problem reading or
     *                                      processing the command line received
     *                                      from the input file.
     */
    public void processCommand(String commandLine, Integer lineNumber) throws CommandProcessorException {
        // Break command line into a list of arguments
        List<String> args = parseCommand(commandLine);

        // First argument is the command to run
        String command = args.remove(0);

        // Pass remaining args into helper methods
        switch (command.toLowerCase()) {
            case "create-ledger":
                createLedger(command, args, lineNumber);
                break;
            case "create-account":
                createAccount(command, args, lineNumber);
                break;
            case "process-transaction":
                processTransaction(command, args, lineNumber);
                break;
            case "get-account-balance":
                getAccountBalance(command, args, lineNumber);
                break;
            case "get-account-balances":
                getAccountBalances(command, lineNumber);
                break;
            case "get-block":
                getBlock(command, args, lineNumber);
                break;
            case "get-transaction":
                getTransaction(command, args, lineNumber);
                break;
            case "validate":
                validate(command, lineNumber);
                break;
            default:
                throw new CommandProcessorException(command, "Unknown command", lineNumber);
        }

        return;
    }

    /**
     * Parses command line by whitespace, keeping quoted arguments intact.
     *
     * As noted by the top answer on StackOverflow (cited below), the copied
     * regex expression looks for
     *      1) Sequences of characters that aren't spaces or quotes
     *      2) And sequences of characters that begin and end with a quote
     *         (for both single (') and double (") quotes).
     *
     * This code (and regex expression) are copied from the example at the URL
     * below. All credit to Jan Goyvaerts and Alan Moore.
     * src: https://stackoverflow.com/a/366532
     *
     * My understanding of the regular expression is as follows:
     *      "[^\\s\"']+     Looks for a string of non-whitespace and non-quote
     *                      characters. The group will stop when any one of those
     *                      is found.
     *      \"([^\"]*)\"    Will look for a double quote ("), followed by any
     *                      number of non-double-quote chars, ending the grouping
     *                      when a double quote is found.
     *      '([^']*)'"      Will look for a single quote ('), followed by any
     *                      number of non-single-quote chars, ending the grouping
     *                      when a single quote is found.
     *
     * @param   commandLine     The command line to parse.
     * @return                  List of arguments, split by whitespace.
     */
    private static List<String> parseCommand(String commandLine) {
        List<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(commandLine);

        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                matchList.add(regexMatcher.group(1));   // double-quoted string
            } else if (regexMatcher.group(2) != null) {
                matchList.add(regexMatcher.group(2));   // single-quoted string
            } else {
                matchList.add(regexMatcher.group());    // unquoted word
            }
        }

        return matchList;
    }

    /**
     * Looks for argument following the specified 'key' in the command line.
     *
     * It is assumed the appropriate argument follows the specified 'key' in
     * the command line and therefore is retrieved by accessing index + 1.
     *
     * @param   key                         The argument to look for.
     * @param   args                        The command line to search.
     * @return  Object                      The Object located at (index + 1)
     *                                      of the 'key'.
     * @throws  IndexOutOfBoundsException   If the 'key' is not in the list of
     *                                      arguments, or if it does not contain
     *                                      a corresponding 'value'.
     */
    private Object getArgument(String key, List<String> args) throws IndexOutOfBoundsException {
        int index;
        Object value = null;

        if ((index = args.indexOf(key)) != -1) {
            value = args.get(index + 1);
        } else {
            throw new IndexOutOfBoundsException();
        }

        return value;
    }

    /**
     * Create a new ledger with the given name, description, and seed value.
     *
     * Expected command line to be formatted as:
     *      create-ledger <name> description <description> seed <seed>
     *
     * @param   command                     The name of the command to be run
     *                                      (used to format Exception).
     * @param   args                        Command line arguments.
     * @param   lineNumber                  Current line number in the file.
     * @throws  CommandProcessorException   If command line arguments are missing,
     *                                      indicated by an IndexOutOfBoundsException.
     */
    private void createLedger(String command, List<String> args, Integer lineNumber) throws CommandProcessorException {
        // Error if Ledger already exists
        if (this.ledger != null) {
            throw new CommandProcessorException(
                command,
                "A Ledger has already been initialized.",
                lineNumber
            );
        }

        try {
            // Look for arguments
            String name = args.get(0);
            String description = (String) getArgument("description", args);
            String seed = (String) getArgument("seed", args);

            // Initialize the Ledger with values
            this.ledger = new Ledger(name, description, seed);
            System.out.println(String.format("Created ledger '%s'", this.ledger));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.", lineNumber);
        } catch (LedgerException e) {
            System.err.println(e);
        }

        return;
    }

    /**
     * Create a new account with the given account id.
     *
     * Expected command line to be formatted as:
     *      create-account <account-id>
     *
     * Additional arguments provided are ignored. A Ledger needs to have been
     * initialized to run this method.
     *
     * @param   command                     The name of the command to be run
     *                                      (used to format Exception).
     * @param   args                        Command line arguments.
     * @param   lineNumber                  Current line number in the file.
     * @throws  CommandProcessorException   If command line arguments are missing,
     *                                      indicated by an IndexOutOfBoundsException.
     *                                      If a Ledger has not been initialized
     *                                      one will be thrown due to a
     *                                      NullPointerException.
     */
    private void createAccount(String command, List<String> args, Integer lineNumber) throws CommandProcessorException {
        // Create account, id == arg[0]
        try {
            Account account = this.ledger.createAccount(args.get(0));
            System.out.println("Created account '" + account.getAddress() + "'");
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing 'account-id'", lineNumber);
        } catch (NullPointerException e) {
            throw new CommandProcessorException(command, "A Ledger has not been initialized.", lineNumber);
        } catch (LedgerException e) {
            System.err.println(e);
        }

        return;
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
     * Extract the required arguments from the command line, retrieve the
     * Accounts from the Ledger, and construct a new Transaction to submit to
     * the Ledger to process. If the Transaction fails to process, a
     * LedgerException is thrown and output to stderr.
     *
     * @see getArgument()
     *
     * @param   command                     The name of the command to be run
     *                                      (used to format Exception).
     * @param   args                        Command line arguments.
     * @param   lineNumber                  Current line number in the file.
     * @throws  CommandProcessorException   If command line arguments are missing,
     *                                      indicated by an IndexOutOfBoundsException.
     *                                      If a Ledger has not been initialized
     *                                      one will be thrown due to a
     *                                      NullPointerException. If an argument
     *                                      is suppose to be an Integer, but no
     *                                      number is provided, a NumberFormatException
     *                                      will be thrown.
     */
    private void processTransaction(String command, List<String> args, Integer lineNumber) throws CommandProcessorException {
        try {
            // Extract arguments from command line
            String id = args.get(0);
            Integer amount = Integer.parseInt((String) getArgument("amount", args));
            Integer fee = Integer.parseInt((String) getArgument("fee", args));
            String payload = (String) getArgument("payload", args);
            String payerAddress = (String) getArgument("payer", args);
            String receiverAddress = (String) getArgument("receiver", args);

            // Check accounts are valid
            Account payer = this.ledger.getExistingAccount(payerAddress);
            Account receiver = this.ledger.getExistingAccount(receiverAddress);

            // Create the transaction
            Transaction newTransaction = new Transaction(id, amount, fee, payload, payer, receiver);

            // Output transaction status
            String result = this.ledger.processTransaction(newTransaction);
            System.out.println("Processed transaction #" + result);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.", lineNumber);
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, e.toString(), lineNumber);
        } catch (NullPointerException e) {
            throw new CommandProcessorException(command, "A Ledger has not been initialized.", lineNumber);
        } catch (LedgerException e) {
            System.err.println(e);
        }

        return;
    }

    /**
     * Output account balances.
     *
     * Expected command line to be formatted as:
     *      get-account-balance <account-id>
     *
     * Handles output of individual accounts, specified by an accountId.
     *
     * @see printAccountBalances
     *
     * @param   command                     The name of the command to be run
     *                                      (used to format Exception).
     * @param   args                        Command line arguments.
     * @param   lineNumber                  Current line number in the file.
     * @throws  CommandProcessorException   If command line arguments are missing,
     *                                      indicated by an IndexOutOfBoundsException.
     *                                      If a Ledger has not been initialized
     *                                      one will be thrown due to a
     *                                      NullPointerException.
     */
    private void getAccountBalance(String command, List<String> args, Integer lineNumber) throws CommandProcessorException {
        // Init a new map to store account balance
        Map<String, Integer> balances = new HashMap<String, Integer>();

        try {
            // Account ID located at arg[0]
            String accountId = args.get(0);
            balances.put(accountId, this.ledger.getAccountBalance(accountId));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.", lineNumber);
        } catch (NullPointerException e) {
            throw new CommandProcessorException(command, "A Ledger has not been initialized.", lineNumber);
        } catch (LedgerException e) {
            System.err.println(e);
        }

        // Print with formatting
        printAccountBalances(balances);
        return;
    }

    /**
     * Output account balances.
     *
     * Expected command line to be formatted as:
     *      get-account-balances
     *
     * Handles output of all accounts, located in the accountBalanceMap of the
     * most recent completed block.
     *
     * @see printAccountBalances
     *
     * @param   command                     The name of the command to be run
     *                                      (used to format Exception).
     * @param   lineNumber                  Current line number in the file.
     * @throws  CommandProcessorException   If command line arguments are missing,
     *                                      indicated by an IndexOutOfBoundsException.
     *                                      If a Ledger has not been initialized
     *                                      one will be thrown due to a
     *                                      NullPointerException.
     */
    private void getAccountBalances(String command, Integer lineNumber) throws CommandProcessorException {
        // Map to store existing information
        Map<String, Integer> balances = null;

        // Get all accounts and balances
        try {
            balances = this.ledger.getAccountBalances();
        } catch (NullPointerException e) {
            throw new CommandProcessorException(command, "A Ledger has not been initialized.", lineNumber);
        } catch (LedgerException e) {
            System.err.println(e);
        }

        // Print with formatting
        printAccountBalances(balances);
        return;
    }

    /**
     * Formats the accountBalanceMap for printing to stdout.
     *
     * For each entry listed in the account balance map, the account address
     * and current balance is extracted, formatted, and output to stdout.
     *
     * @param balances  The account balance map to iterate and format.
     */
    private void printAccountBalances(Map<String, Integer> balances) {
        // Skip printing if no balances
        if (balances != null) {
            // Get address and balance for each account entry
            for (Map.Entry<String, Integer> account : balances.entrySet()) {
                System.out.println(String.format(
                        "Account %s: current balance = %d",
                        account.getKey(), account.getValue()
                ));
            }
        }
    }

    /**
     * Output the details for the given block number.
     *
     * Expected command line to be formatted as:
     *      get-block <block-number>
     *
     * @param   command                     The name of the command to be run
     *                                      (used to format Exception).
     * @param   args                        Command line arguments.
     * @param   lineNumber                  Current line number in the file.
     * @throws  CommandProcessorException   If command line arguments are missing,
     *                                      indicated by an IndexOutOfBoundsException.
     *                                      If a Ledger has not been initialized
     *                                      one will be thrown due to a
     *                                      NullPointerException. If a Block does
     *                                      not exist.
     */
    private void getBlock(String command, List<String> args, Integer lineNumber) throws CommandProcessorException {
        try {
            Integer blockNumber = Integer.parseInt(args.get(0));
            Block block = this.ledger.getBlock(blockNumber);
            System.out.print(block);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments", lineNumber);
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, args.get(0) + " is not a number.", lineNumber);
        } catch (NullPointerException e) {
            throw new CommandProcessorException(command, "A Ledger has not been initialized.", lineNumber);
        } catch (LedgerException e) {
            System.err.println(e);
        }

        return;
    }

    /**
     * Output the details of the given transaction id.
     *
     * Expected command line to be formatted as:
     *      get-transaction <transaction-id>
     *
     * @param   command                     The name of the command to be run
     *                                      (used to format Exception).
     * @param   args                        Command line arguments.
     * @param   lineNumber                  Current line number in the file.
     * @throws  CommandProcessorException   If command line arguments are missing,
     *                                      indicated by an IndexOutOfBoundsException.
     *                                      If a Ledger has not been initialized
     *                                      one will be thrown due to a
     *                                      NullPointerException. If a Transaction
     *                                      is not found in the Ledger.
     */
    private void getTransaction(String command, List<String> args, Integer lineNumber) throws CommandProcessorException {
        try {
            String transactionId = args.get(0);
            Transaction transaction = this.ledger.getTransaction(transactionId);
            System.out.println(transaction);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments", lineNumber);
        } catch (NullPointerException e) {
            throw new CommandProcessorException(command, "A Ledger has not been initialized.", lineNumber);
        } catch (LedgerException e) {
            throw new CommandProcessorException(command, e.getReason(), lineNumber);
        }

        return;
    }

    /**
     * Validate the current state of the block chain. On success, message will
     * be printed to stdout. On LedgerException (i.e. blockchain is _not_ valid),
     * the Exception will be printed to stderr.
     *
     * Expected command line to be formatted as:
     *      validate
     *
     * @see Ledger#validate
     *
     * @param   command                     The name of the command to be run
     *                                      (used to format Exception).
     * @param   lineNumber                  Current line number in the file.
     * @throws  CommandProcessorException   If a Ledger has not been initialized
     *                                      one will be thrown due to a
     *                                      NullPointerException.
     */
    private void validate(String command, Integer lineNumber) throws CommandProcessorException {
        try {
            this.ledger.validate();
        } catch (NullPointerException e) {
            throw new CommandProcessorException(command, "A Ledger has not been initialized.", lineNumber);
        } catch (LedgerException e) {
            System.err.println(e);
        }

        System.out.println("Blockchain successfully validated.");
        return;
    }
}