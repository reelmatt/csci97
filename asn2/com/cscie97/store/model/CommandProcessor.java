package com.cscie97.store.model;

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
    private StoreModelService storeModelService = null;

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

        storeModelService = new StoreModelService();

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
        if (this.storeModelService == null) {
            throw new CommandProcessorException("command", "StoreModelService has not been initialized.", lineNumber);
        }

        // Break command line into a list of arguments
        List<String> args = parseCommand(commandLine);

        // Key syntax variables
        String command = null;
        String object = null;
        String id = null;

        try {
            // First argument is the command to run
            command = args.remove(0);

            // Second argument is the object (Store, Aisle, etc.)
            object = args.remove(0);

            // Third argument is the identifier (storeId, aisleId, etc.)
            id = args.remove(0);

        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(commandLine, "Missing arguments.", lineNumber);
        }

        // Pass remaining args into helper methods
        try {
            switch (object.toLowerCase()) {
                case "store":
                    store(command, object, id, args);
                    break;
                case "aisle":
                    aisle(command, object, id, args);
                    break;
                case "shelf":
                    shelf(command, object, id, args);
                    break;
                case "inventory":
                    inventory(command, object, id, args);
                    break;
                case "product":
    //                product(command, object, id, args);
                    break;
                case "customer":
    //                customer(command, object, id, args);
                    break;
                case "device":
                    device(command, object, id, args);
                    break;
                case "basket":
                    break;
                case "basket_item":
                    break;
                case "event":
                    break;
                case "command":
                    break;
                default:
                    throw new CommandProcessorException(command, "Unknown command", lineNumber);
            }

        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

//        return;
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


    private void store(String command, String object, String id, List<String> args) throws CommandProcessorException, StoreModelServiceException {
        switch(command) {
            case "define":
                defineStore(command, id, args);
                break;
            case "show":
                this.storeModelService.showStore(id);
                break;
            default:
                throw new CommandProcessorException(command + " " + object, "Unknown command.");
        }
    }

    private void aisle(String command, String object, String id, List<String> args) throws CommandProcessorException, StoreModelServiceException {

        switch(command) {
            case "define":
                defineAisle(command, id, args);
                break;
            case "show":
                this.storeModelService.showAisle(id);
                break;
            default:
                throw new CommandProcessorException(command + " " + object, "Unknown command.");
        }

        return;
    }

    private void shelf(String command, String object, String id, List<String> args) throws CommandProcessorException, StoreModelServiceException {

        switch(command) {
            case "define":
                defineShelf(command, id, args);
                break;
            case "show":
                this.storeModelService.showShelf(id);
                break;
            default:
                throw new CommandProcessorException(command + " " + object, "Unknown command.");
        }
        return;
    }

    private void inventory(String command, String object, String id, List<String> args) throws CommandProcessorException, StoreModelServiceException {

        switch(command) {
            case "define":
                defineInventory(command, id, args);
                break;
            case "show":
                this.storeModelService.showInventory(id);
                break;
            default:
                throw new CommandProcessorException(command + " " + object, "Unknown command.");
        }

        return;
    }

    private void device(String command, String object, String id, List<String> args) throws CommandProcessorException, StoreModelServiceException {

        switch(command) {
            case "define":
                defineDevice(command, id, args);
                break;
            case "show":
                this.storeModelService.showDevice(id);
                break;
            default:
                throw new CommandProcessorException(command + " " + object, "Unknown command.");
        }

        return;
    }

    private void defineStore(String command, String id, List<String> args) throws CommandProcessorException {
        try {
            String name = (String) getArgument("name", args);
            String address = (String) getArgument("address", args);
            this.storeModelService.defineStore(id, name, address);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, e.toString());
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    }

    private void defineAisle(String command, String id, List<String> args) throws CommandProcessorException {
        try {
            String name = (String) getArgument("name", args);
            String description = (String) getArgument("description", args);
            String location = (String) getArgument("location", args);

            this.storeModelService.defineAisle(id, name, description, location);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

    }

    private void defineShelf(String command, String id, List<String> args) throws CommandProcessorException {
        System.out.println("SHELF:");
        try {
            String name = (String) getArgument("name", args);
            String description = (String) getArgument("description", args);
            String level = (String) getArgument("level", args);
            String temperature = (String) getArgument("temperature", args);

            this.storeModelService.defineShelf(id, name, level, description, temperature);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

    }

    private void defineInventory(String command, String id, List<String> args) throws CommandProcessorException {
        try {
            String location = (String) getArgument("location", args);
            String capacity = (String) getArgument("capacity", args);
            String count = (String) getArgument("count", args);
            String productId = (String) getArgument("product", args);

            this.storeModelService.defineInventory(id, location, Integer.parseInt(capacity), Integer.parseInt(count), productId);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Argument is not a valid Integer.");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

    }

    private void defineDevice(String command, String id, List<String> args) throws CommandProcessorException {
        try {
            String name = (String) getArgument("name", args);
            String type = (String) getArgument("type", args);
            String location = (String) getArgument("location", args);

            this.storeModelService.defineDevice(id, name, type, location);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Argument is not a valid Integer.");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

    }

    private void defineProduct(String command, String id, List<String> args) throws CommandProcessorException {
        try {
            String name = (String) getArgument("name", args);
            String description = (String) getArgument("description", args);
            String size = (String) getArgument("size", args);
            String category = (String) getArgument("category", args);
            String unitPrice = (String) getArgument("unitPrice", args);
            String temperature = (String) getArgument("temperature", args);

            this.storeModelService.defineProduct(
                    id,
                    name,
                    description,
                    Integer.parseInt(size),
                    category,
                    Integer.parseInt(unitPrice),
                    temperature
            );
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Argument is not a valid Integer.");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

    }
}