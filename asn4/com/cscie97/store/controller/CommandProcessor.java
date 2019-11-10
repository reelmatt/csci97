package com.cscie97.store.controller;

import com.cscie97.store.model.*;
import com.cscie97.ledger.*;
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
 * CommandProcessor - Utility class to feed a StoreModelService a set of operations.
 *
 * This class is used by the TestDriver to open a file containing a set of
 * commands. It then processes each command, outputting a formatted response,
 * or the error message from the caught Exception. It contains several helper
 * methods to process a command for each publically-accessible API of the
 * StoreModelService.
 *
 * @author Matthew Thomas
 */
public class CommandProcessor {
    /** StoreControllerService to monitor store state and control appliances. */
    private StoreControllerService storeControllerService = null;

    /** StoreModelService to create, read, and update Store objects. */
    private StoreModelServiceInterface storeModelService = null;

    /** Ledger to manage accounts and process transactions. */
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

        // Create Store Model Service
        this.storeModelService = new StoreModelService();

        // Create Ledger
        try {
            this.ledger = new Ledger("test", "test ledger", "cambridge");
        } catch (LedgerException e) {
            System.err.println(e);
        }

        // Create Store Controller Service with Model and Ledger
        this.storeControllerService = new StoreControllerService(this.storeModelService, this.ledger);

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
     * StoreModelServiceExceptions are caught with their contents output to stderr.
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
        // StoreControllerService instance needs to be created to run commands
        if (this.storeControllerService == null) {
            throw new CommandProcessorException("command", "StoreControllerService has not been initialized.", lineNumber);
        }

        // Break command line into a list of arguments
        List<String> args = parseCommand(commandLine);

        // Key syntax variable
        String command = null;

        try {
            // First argument is the command to run
            command = args.remove(0);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(commandLine, "Missing arguments.", lineNumber);
        }

        // Check if command matches a Ledger command
        if ( processLedgerCommand(command, args, lineNumber) ) {
            // It was a Ledger command, so return
            return;
        }

        // Key syntax variables for Model service
        String storeObject = null;
        String id = null;
        String authToken = "authToken is part of Assignment 4";

        // Extract additional key arguments
        try {
            // Second argument is the object (Store, Aisle, etc.)
            storeObject = args.remove(0);

            // Third argument is the identifier (storeId, aisleId, etc.)
            id = args.remove(0);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(commandLine, "Missing arguments.", lineNumber);
        }

        // Process Store Model Command
        processStoreModelCommand(authToken, command, storeObject, id, args, lineNumber);
        return;
    }

    /**
     * Process a single Ledger command.
     *
     * @param   command                     The current command to process
     * @param   args                        The remaining args in the command line.
     * @param   lineNumber                  The current line number in the file.
     * @return                              True if Ledger command was matched.
     *                                      Otherwise, false.
     * @throws  CommandProcessorException   If there is a problem reading or
     *                                      processing the command line received
     *                                      from the input file.
     */
    private boolean processLedgerCommand(String command, List<String> args, Integer lineNumber) throws CommandProcessorException {
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
                // was not a Ledger command, return false to try Store Model command
                return false;
        }
        return true;
    }

    /**
     * Process a single Store Model Service command.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   command                     The current command to process.
     * @param   storeObject                 The name of the Store Model object to modify/create.
     * @param   id                          The storeObject identifier.
     * @param   args                        The remaining args in the command line.
     * @param   lineNumber                  The current line number in the file.
     * @throws  CommandProcessorException   If there is a problem reading or
     *                                      processing the command line received
     *                                      from the input file.
     */
    private void processStoreModelCommand(String authToken, String command, String storeObject, String id, List<String> args, Integer lineNumber) throws CommandProcessorException{
        // Pass remaining args into helper methods
        try {
            switch (command.toLowerCase()) {
                case "add":
                    addBasketItem(authToken, command, id, args);
                    break;
                case "clear":
                    clearBasket(authToken, id);
                    break;
                case "create":
                    create(authToken, command, storeObject, id, args);
                    break;
                case "define":
                    define(authToken, command, storeObject, id, args);
                    break;
                case "get":
                    getBasket(authToken, command, id, args);
                    break;
                case "remove":
                    removeItem(authToken, command, id, args);
                    break;
                case "show":
                    show(authToken, command, storeObject, id, args);
                    break;
                case "update":
                    update(authToken, command, storeObject, id, args);
                    break;
                default:
                    throw new CommandProcessorException(command, "Unknown command", lineNumber);
            }
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }
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
     * Behavior modified for Assignment 2 to return 'null' when the specified
     * key is not found. This allows for optional parameters, like 'temperature'
     * for defining a new Product. When a key is present, but no corresponding
     * value, an IndexOutOfBoundsException is still thrown.
     *
     * @param   key                         The argument to look for.
     * @param   args                        The command line to search.
     * @return  String                      The String located at (index + 1)
     *                                      of the 'key'. If 'key' is not in the
     *                                      list of arguments, null is returned.
     * @throws  IndexOutOfBoundsException   If the 'key' does not contain a
     *                                      corresponding 'value'.
     */
    private String getArgument(String key, List<String> args) throws IndexOutOfBoundsException {
        int index;

        if ((index = args.indexOf(key)) != -1) {
            return args.get(index + 1);
        } else {
            return null;
        }
    }


    /**
     * Add a Product item to a Basket.
     *
     * Expected command line to be formatted as:
     *      add basket_item <customer_id> product <product_id> item_count <count>
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param customerId                    The customer ID to use.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfBoundsException. Or, if value provided for item
     *                                      count is not a valid Integer, as indicated by
     *                                      NumberFormatException.
     * @throws StoreModelServiceException   If the call to addItemToBasket() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void addBasketItem (String authToken, String command, String customerId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to add basket item
        String productId = null;
        Integer itemCount = null;

        // Check for argument exceptions
        try {
            productId = getArgument("product", args);
            itemCount = Integer.parseInt(getArgument("item_count", args));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Invalid item count.");
        }

        // Add the item to basket
        ProductAssociation basketItem = this.storeModelService.addItemToBasket(authToken, customerId, productId, itemCount);

        // Print current count of item in basket
        System.out.println("Added " + productId + " to the basket. Current count is " + basketItem.getCount() + ".");
        return;
    }

    /**
     * Clear the contents of a basket and remove its association from the Customer.
     *
     * Expected command line to be formatted as:
     *      clear basket <customer_id>
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param customerId                    The customer ID to use.
     * @throws StoreModelServiceException   If the call to addItemToBasket() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void clearBasket (String authToken, String customerId) throws StoreModelServiceException {
        this.storeModelService.clearBasket(authToken, customerId);
        System.out.println("Customer '" + customerId + "' basket cleared and removed.");
    }

    /**
     * Helper function to respond to 'create' commands.
     *
     * This function handles any command line starting with the keyword 'create'
     * and handles creation of 'commands' or 'events' that are sent or received
     * by store Devices.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param object                        The name of the Store object to create.
     * @param id                            The identifier of the Store object to create.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If the 'object' is not a known Store entity listed
     *                                      in the switch-statment. Also thrown by the sub methods
     *                                      if command line arguments are missing.
     * @throws StoreModelServiceException   If a call to the Model Service in any of the helper
     *                                      functions fails, this Exception is thrown and passed
     *                                      up to by handled and printed in the processCommand()
     *                                      method.
     */
    private void create(String authToken, String command, String object, String id, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        switch (object.toLowerCase()) {
            case "command":
                createCommand(authToken, command, id, args);
                break;
            case "event":
                createEvent(authToken, command, id, args);
                break;
            default:
                throw new CommandProcessorException(command, "Unknown command");
        }

    }

    /**
     * Send a command to an Appliance.
     *
     * Expected command line to be formatted as:
     *      create command <device_id> message <command>
     *
     * The message is extracted from the command line and sent to the Appliance to then
     * respond to. Command messages can be issued by Customers (e.g. "Where is the milk?")
     * or by the Store Controller (e.g. "Checkout customers"). For this assignment, commands
     * are treated as opaque strings and printed to stdout to indicate receipt of message.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param deviceId                      The device ID to use.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfboundsException.
     * @throws StoreModelServiceException   If the call to commandAppliance() fails, the Model
     *                                      Service will throw an Exception, which is handled and
     *                                      printed in the processCommand() method.
     */
    private void createCommand(String authToken, String command, String deviceId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to add basket item
        String message = null;

        // Check for argument exceptions
        try {
            message = getArgument("message", args);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        }

        // Send command to Appliance, which will respond itself
        this.storeModelService.receiveCommand(authToken, deviceId, message);
    }

    /**
     * Create a Sensor event (simulating a real Sensor event).
     *
     * Expected command line to be formatted as:
     *      create event <device_id> event <event>
     *
     * The message is extracted from the command line and sent to the Appliance to then
     * respond to. Command messages can be issued by Customers (e.g. "Where is the milk?")
     * or by the Store Controller (e.g. "Checkout customers"). For this assignment, commands
     * are treated as opaque strings and printed to stdout to indicate receipt of message.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param deviceId                      The device ID to use.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfboundsException.
     * @throws StoreModelServiceException   If the call to getDevice() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void createEvent (String authToken, String command, String deviceId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get the opaque event string
        String event = null;

        // Check for argument exceptions
        try {
            event = getArgument("event", args);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        }

        // Check deviceId references a valid device
        Device device = this.storeModelService.getDevice(authToken, deviceId);

        // Send simulated event to the Store Model Service
        this.storeModelService.receiveEvent(authToken, deviceId, event);
    }

    /**
     * Helper function to define a specified Store entity.
     *
     * This function handles any command line starting with the keyword 'define'
     * and handles creation of Store entities specified by the second argument,
     * the 'object'. If the 'object' is not an option in the switch-statement,
     * that entity either does not exist, or a public API in the Store Model
     * Service does not exist. All creation steps are handled by more granular
     * helper methods.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param object                        The name of the Store object to create.
     * @param id                            The identifier of the Store object to create.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If the 'object' is not a known Store entity listed
     *                                      in the switch-statment. Also thrown by the sub methods
     *                                      if command line arguments are missing.
     * @throws StoreModelServiceException   If a call to the Model Service in any of the helper
     *                                      functions fails, this Exception is thrown and passed
     *                                      up to by handled and printed in the processCommand()
     *                                      method.
     */
    private void define(String authToken, String command, String object, String id, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        switch (object.toLowerCase()) {
            case "aisle":
                defineAisle(authToken, command, id, args);
                break;
            case "customer":
                defineCustomer(authToken, command, id, args);
                break;
            case "device":
                defineDevice(authToken, command, id, args);
                break;
            case "inventory":
                defineInventory(authToken, command, id, args);
                break;
            case "product":
                defineProduct(authToken, command, id, args);
                break;
            case "shelf":
                defineShelf(authToken, command, id, args);
                break;
            case "store":
                defineStore(authToken, command, id, args);
                break;
            default:
                throw new CommandProcessorException(command, "Unknown command");
        }
    }

    /**
     * Define a new Aisle.
     *
     * Expected command line to be formatted as:
     *      define aisle <store_id>:<aisle_number>
     *             name <name>
     *             description <description>
     *             location (floor | store_room)
     *
     * Information that is needed to create an Aisle is extracted from command
     * line arguments and stored in a Map. The values are then passed to the
     * StoreModelService which defines the object and maintains the overall state
     * of the Store 24X7 System.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param aisleId                       The fully-qualified aisle ID.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfBoundsException, caught and rethrown in the
     *                                      getStoreEntityInfo() method.
     * @throws StoreModelServiceException   If the call to defineAisle() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void defineAisle(String authToken, String command, String aisleId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to define an Aisle
        String[] keys = {"name", "description", "location"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        // Add the Aisle to the ModelService
//        try {
            Aisle aisle = this.storeModelService.defineAisle(
                    authToken,
                    aisleId,
                    entityInfo.get("name"),
                    entityInfo.get("description"),
                    (Location) getEnum(Location.values(), entityInfo.get("location"))
            );

            printCreatedEntity("aisle", aisle.getId());
//        } catch (NullPointerException e) {
//            throw new CommandProcessorException(command, "Location type unknown.");
//        }

    }

    /**
     * Define a new Customer.
     *
     * Expected command line to be formatted as:
     *      define customer <customer_id>
     *             first_name <first_name>
     *             last_name <last_name>
     *             type (registered | guest)
     *             email_adress <email>
     *             account <account_address>
     *
     * Information that is needed to create an Customer is extracted from command
     * line arguments and stored in a Map. The values are then passed to the
     * StoreModelService which defines the object and maintains the overall state
     * of the Store 24X7 System.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param customerId                    The customer ID.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfBoundsException, caught and rethrown in the
     *                                      getStoreEntityInfo() method.
     * @throws StoreModelServiceException   If the call to defineCustomer() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void defineCustomer(String authToken, String command, String customerId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to define a Customer
        String[] keys = {"first_name", "last_name", "type", "email_address", "account"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        // Add the Customer to the ModelService
        Customer customer = this.storeModelService.defineCustomer(
                authToken,
                customerId,
                entityInfo.get("first_name"),
                entityInfo.get("last_name"),
                (CustomerType) getEnum(CustomerType.values(), entityInfo.get("type")),
                entityInfo.get("email_address"),
                entityInfo.get("account")
        );

        printCreatedEntity("customer", customer.getId());
    }

    /**
     * Define a new Device.
     *
     * Expected command line to be formatted as:
     *      define device <device_id>
     *             name <name>
     *             type (microphone | camera | speaker | robot | turnstile)
     *             location <store_id>:<aisle_id>
     *
     * Information that is needed to create an Device is extracted from command
     * line arguments and stored in a Map. The values are then passed to the
     * StoreModelService which defines the object and maintains the overall state
     * of the Store 24X7 System.
     *
     * A Device is subclassed as a Sensor or Appliance. The information remains
     * the same for either Device. The allowable type options for a Sensor are
     * microphone and camera, and the allowable type options for an Appliance
     * are speaker, robot, or turnstile.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param deviceId                      The device ID.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfBoundsException, caught and rethrown in the
     *                                      getStoreEntityInfo() method.
     * @throws StoreModelServiceException   If the call to defineDevice() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void defineDevice(String authToken, String command, String deviceId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to define a Device
        String[] keys = {"name", "type", "location"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        // Add the Device to the ModelService
        Device device = this.storeModelService.defineDevice(
                authToken,
                deviceId,
                entityInfo.get("name"),
                entityInfo.get("type"),
                entityInfo.get("location")
        );

        printCreatedEntity("device", device.getId());
    }

    /**
     * Define a new Inventory.
     *
     * Expected command line to be formatted as:
     *      define inventory <inventory_id>
     *             location <store_id>:<aisle_id>:<shelf_id>
     *             name <name>
     *             level (high | medium | low)
     *             description <description>
     *             [temperature (frozen | refrigerated | ambient | warm | hot)]
     *
     * Information that is needed to create an Inventory object is extracted from
     * command line arguments and stored in a Map. The values are then passed to the
     * StoreModelService which defines the object and maintains the overall state
     * of the Store 24X7 System. The 'temperature' argument is optional, and will
     * be assigned a default value if not specified.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param inventoryId                   The inventory ID.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfBoundsException, caught and rethrown in the
     *                                      getStoreEntityInfo() method. Or, if the values provided
     *                                      for capacity or count are not valid Integers, as indicated
     *                                      by NumberFormatException.
     * @throws StoreModelServiceException   If the call to defineInventory() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void defineInventory(String authToken, String command, String inventoryId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to define an Inventory object
        String[] keys = {"location", "capacity", "count", "product"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        // Validate Integer arguments
        Integer capacity, count = null;
        try {
            capacity = Integer.parseInt(entityInfo.get("capacity"));
            count = Integer.parseInt(entityInfo.get("count"));
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Argument is not a valid Integer.");
        }

        // Add the Inventory to the ModelService
        Inventory inventory = this.storeModelService.defineInventory(
                authToken,
                inventoryId,
                entityInfo.get("location"),
                capacity,
                count,
                entityInfo.get("product")
        );

        printCreatedEntity("inventory", inventory.getId());
    }

    /**
     * Define a new Product.
     *
     * Expected command line to be formatted as:
     *      define product <product_id>
     *             name <name>
     *             description <description>
     *             size <size>
     *             category <category>
     *             unit_price <unit_price>
     *             [temperature (frozen | refrigerated | ambient | warm | hot)]
     *
     * Information that is needed to create a Product is extracted from
     * command line arguments and stored in a Map. The values are then passed to the
     * StoreModelService which defines the object and maintains the overall state
     * of the Store 24X7 System. The 'temperature' argument is optional, and will
     * be assigned a default value if not specified.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param customerId                    The customer ID.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfBoundsException, caught and rethrown in the
     *                                      getStoreEntityInfo() method. Or, if the value provided
     *                                      for unit_price is not a valid Integer, as indicated
     *                                      by NumberFormatException.
     * @throws StoreModelServiceException   If the call to defineProduct() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void defineProduct(String authToken, String command, String productId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to define a Product
        String[] keys = {"name", "description", "size", "category", "unit_price", "temperature"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        // Validate unit_price and size arguments
        Integer unitPrice = null;
        Double size = null;
        try {
            unitPrice = Integer.parseInt(entityInfo.get("unit_price"));
            size =  Double.parseDouble(entityInfo.get("size"));
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Unit price is not a valid Integer.");
        }

        // Add the Product to the ModelService
        Product product = this.storeModelService.defineProduct(
                authToken,
                productId,
                entityInfo.get("name"),
                entityInfo.get("description"),
                size,
                entityInfo.get("category"),
                unitPrice,
                (Temperature) getEnum(Temperature.values(), entityInfo.get("temperature"))
        );

        printCreatedEntity("product", product.getId());
    }

    /**
     * Define a new Shelf.
     *
     * Expected command line to be formatted as:
     *      define shelf <store_id>:<aisle_id>:<shelf_id>
     *             name <name>
     *             level (high | medium | low)
     *             description <description>
     *             [temperature (frozen | refrigerated | ambient | warm | hot)]
     *
     * Information that is needed to create an Customer is extracted from command
     * line arguments and stored in a Map. The values are then passed to the
     * StoreModelService which defines the object and maintains the overall state
     * of the Store 24X7 System.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param customerId                    The customer ID.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfBoundsException, caught and rethrown in the
     *                                      getStoreEntityInfo() method.
     * @throws StoreModelServiceException   If the call to defineShelf() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void defineShelf(String authToken, String command, String shelfId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to define a Shelf
        String[] keys = {"name", "description", "level", "temperature"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        // Add the Shelf to the ModelService
        Shelf shelf = this.storeModelService.defineShelf(
                authToken,
                shelfId,
                entityInfo.get("name"),
                (Level) getEnum(Level.values(), entityInfo.get("level")),
                entityInfo.get("description"),
                (Temperature) getEnum(Temperature.values(), entityInfo.get("temperature"))
        );

        printCreatedEntity("shelf", shelf.getId());
    }

    /**
     * Define a new Store.
     *
     * Expected command line to be formatted as:
     *      define store <store-id>
     *
     * Information that is needed to create a Store is extracted from command
     * line arguments and stored in a Map. The values are then passed to the
     * StoreModelService which defines the object and maintains the overall state
     * of the Store 24X7 System.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param storeId                       The store ID.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfBoundsException, caught and rethrown in the
     *                                      getStoreEntityInfo() method.
     * @throws StoreModelServiceException   If the call to defineStore() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void defineStore(String authToken, String command, String storeId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to define a Store
        String[] keys = {"name", "address"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        // Add the Store to the ModelService
        Store store = this.storeModelService.defineStore(
                authToken,
                storeId,
                entityInfo.get("name"),
                entityInfo.get("address")
        );

        printCreatedEntity("store", store.getId());
    }

    /**
     * Retrieve the basket associated with the specified Customer. If one does not
     * exist, create the basket and associate with the Customer.
     *
     * Expected command line to be formatted as:
     *      get customer_basket <customer_id>
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param customerId                    The customer ID to use.
     * @param args                          Command line arguments.
     * @throws StoreModelServiceException   If the call to getCustomerBasket() or
     *                                      createCustomerBasket() fails, the Model Service will
     *                                      throw an Exception, which is handled and printed in
     *                                      the processCommand() method.
     */
    private void getBasket (String authToken, String command, String customerId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Check to see if the Customer already has a basket

        Basket basket = null;
        try {
            basket = this.storeModelService.getBasket(authToken, customerId);
        } catch (StoreModelServiceException e) {
            System.out.println("Customer '" + customerId + "' does not have a basket. Creating one.");
        }

        // Does not have one
        if (basket == null) {
            // Create a new one
            basket = this.storeModelService.defineBasket(authToken, customerId);
            System.out.println("Basket created for Customer '" + customerId + "'");
        } else {
            System.out.println("Customer '" + customerId + "' has an associated basket.");
        }
    }

    private void printAisleList(List<Aisle> aisles) {
        if (aisles.size() == 0) {
            System.out.println("The store has 0 aisles.");
        } else {
            aisles.forEach((aisle) -> System.out.println(aisle));
        }
    }

    private void printCustomerList(List<Customer> customers) {
        if (customers.size() == 0) {
            System.out.println("The store has 0 customers.");
        } else {
            customers.forEach((customer) -> System.out.println(customer));
        }
    }

    private void printDeviceList(List<Device> devices) {
        if (devices.size() == 0) {
            System.out.println("The store has 0 devices.");
        } else {
            devices.forEach((device) -> System.out.println(device));
        }
    }

    private void printInventoryList(List<Inventory> inventories) {
        if (inventories.size() == 0) {
            System.out.println("The shelf has 0 inventories.");
        } else {
            inventories.forEach((inventory) -> System.out.println(inventory));
        }
    }

    private void printShelfList(List<Shelf> shelves) {
        if (shelves.size() == 0) {
            System.out.println("The aisle has 0 shelves.");
        } else {
            shelves.forEach((shelf) -> System.out.println(shelf));
        }
    }

    /**
     * Remove a count of a Product from a basket.
     *
     * Expected command line to be formatted as:
     *      remove basket_item <customer_id> product <product_id> item_count <count>
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param customerId                    The customer ID to use.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If command line arguments are missing, indicated by an
     *                                      IndexOutOfBoundsException. Or, if value provided for item
     *                                      count is not a valid Integer, as indicated by
     *                                      NumberFormatException.
     * @throws StoreModelServiceException   If the call to removeItemFromBasket() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void removeItem (String authToken, String command, String customerId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to add basket item
        String productId = null;
        Integer itemCount = null;

        // Check for argument exceptions
        try {
            productId = getArgument("product", args);
            itemCount = Integer.parseInt(getArgument("item_count", args));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Invalid item count.");
        }

        // Remove the item to basket
        ProductAssociation basketItem = this.storeModelService.removeItemFromBasket(authToken, customerId, productId, itemCount);

        // Format and print the amount remaining
        String status = "Removed " + itemCount + " " + productId + " from the basket. ";
        if (basketItem == null) {
            System.out.println(status + "No more of this item remains in the basket.");
            return;
        }

        // Print current count of item in basket
        System.out.println(status + "Current count is " + basketItem.getCount() + ".");
        return;
    }

    /**
     * Helper function to retrieve a specified Store entity and print the details.
     *
     * This function handles any command line starting with the keyword 'show'
     * and handles the display of Store entity details, specified by the second
     * argument, the 'object'.
     *
     * Retrieval and display of Customer, Device, or Product objects are done in
     * this method. Objects which allow for optional specificity or multiple
     * results -- Aisle, Basket Items, Inventory, Shelf, and Store -- are handled
     * by sub-methods.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param object                        The name of the Store object to display.
     * @param id                            The identifier of the Store entity to retrieve.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    If the 'object' is not a known Store entity listed
     *                                      in the switch-statment. Also thrown by the sub methods
     *                                      if command line arguments are missing.
     * @throws StoreModelServiceException   If a call to the Model Service in any of the helper
     *                                      functions fails, this Exception is thrown and passed
     *                                      up to by handled and printed in the processCommand()
     *                                      method.
     */
    private void show(String authToken,
                      String command,
                      String object,
                      String id,
                      List<String> args)
            throws CommandProcessorException, StoreModelServiceException {

        switch (object.toLowerCase()) {
            case "aisle":
                showAisle(authToken, command, id);
                break;
            case "basket_items":
                showBasketItems(authToken, id);
                break;
            case "customer":
                System.out.println(this.storeModelService.getCustomer(authToken, id));
                break;
            case "device":
                System.out.println(this.storeModelService.getDevice(authToken, id));
                break;
            case "inventory":
                showInventory(authToken, command, id);
                break;
            case "product":
                System.out.println(this.storeModelService.getProduct(authToken, id));
                break;
            case "shelf":
                showShelf(authToken, command, id);
                break;
            case "store":
                showStore(authToken, id);
                break;
            default:
                throw new CommandProcessorException(command, "Unknown command");
        }
    }

    /**
     * Show details of an Aisle including the name, description, and list of shelves.
     *
     * Expected command line to be formatted as:
     *      show aisle <store_id>[:<aisle_id>]
     *
     * If the location string just includes the store_id, information is printed
     * to stdout for all Aisles (if any) within the store. If both store and aisle
     * ids are specified, information is printed only for the specified Aisle.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used to format Exception).
     * @param location                      The location string, delimited by ':' colons.
     * @throws CommandProcessorException    If the location string contains zero id 'tokens' after
     *                                      parsing on ':' colons.
     * @throws StoreModelServiceException   If the call to getStore() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void showAisle(String authToken, String command, String location)
            throws CommandProcessorException, StoreModelServiceException {
        // Parse the location String
        String[] ids = parseLocationIdentifier(location);

        // No location arguments
        if (ids.length == 0) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } else if (ids.length == 1) {
            // Just a store_id -- show all aisles
            Store store = this.storeModelService.getStore(authToken, location);
            printAisleList(store.getAisleList());
        } else {
            // Specific Aisle is referenced
            Aisle aisle = this.storeModelService.getAisle(authToken, location);
            System.out.println(aisle);
        }
    }

    private void showBasketItems(String authToken, String customerId) throws StoreModelServiceException {
        Basket basket = this.storeModelService.getBasket(authToken, customerId);


        List<ProductAssociation> basketItems = basket.getBasketItems();

        if (basketItems.size() == 0) {
            System.out.println("The basket contains 0 items.");
        } else {
            basketItems.forEach((item) -> System.out.println(item));
        }
    }

    /**
     * Show details of an Inventory, including the id, product name, capacity,
     * and count.
     *
     * Expected command line to be formatted as:
     *      show inventory <store_id>[:<aisle_id>[:<shelf_id>[:<inventory_id>]]]
     *
     * The Aisle, Shelf, and Inventory ids are all optional. Depending on the
     * specificity, details of an Inventory will be printed to stdout for a
     * given inventory_id, or all inventories (if any) located within a shelf,
     * aisle, or store.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used to format Exception).
     * @param location                      The location string, delimited by ':' colons.
     * @throws CommandProcessorException    If the location string contains zero id 'tokens' after
     *                                      parsing on ':' colons.
     * @throws StoreModelServiceException   If the call to getInventory() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void showInventory(String authToken, String command, String location)
            throws CommandProcessorException, StoreModelServiceException {
        // Parse the location String
        String[] ids = parseLocationIdentifier(location);

        if (ids.length == 0) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } else if (ids.length == 1) {
            // Only a storeid
            Store store = this.storeModelService.getStore(authToken, location);
            printAisleList(store.getAisleList());
        } else if (ids.length == 2) {
            // Store and aisle ids
            Aisle aisle = this.storeModelService.getAisle(authToken, location);
            printShelfList(aisle.getShelfList());
        } else if (ids.length == 3) {
            // Store, aisle, and shelf ids
            Shelf shelf = this.storeModelService.getShelf(authToken, location);
            printInventoryList(shelf.getInventoryList());
        } else {
            // Specific inventory is referenced
            Inventory inventory = this.storeModelService.getInventory(authToken, location);
            System.out.println(inventory);
        }
    }

    /**
     * Show details of an Shelf, including the id, name, level, description,
     * and temperature.
     *
     * Expected command line to be formatted as:
     *      show shelf <store_id>[:<aisle_id>[:<shelf_id>]]
     *
     * The Aisle and Shelf ids are optional. Depending on the specificty,
     * details of a Shelf will be printed to stdout for a given shelf_id, or
     * all shelves (if any) located within a aisle or store.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used to format Exception).
     * @param location                      The location string, delimited by ':' colons.
     * @throws CommandProcessorException    If the location string contains zero id 'tokens' after
     *                                      parsing on ':' colons.
     * @throws StoreModelServiceException   If the call to getShelf() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void showShelf(String authToken, String command, String location)
            throws CommandProcessorException, StoreModelServiceException {
        // Parse the location String
        String[] ids = parseLocationIdentifier(location);

        // Only a store ID
        if (ids.length == 0) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } else if (ids.length == 1) {
            // Only a store id
            Store store = this.storeModelService.getStore(authToken, location);
            printAisleList(store.getAisleList());
        } else if (ids.length == 2) {
            // Store and aisle ids
            Aisle aisle = this.storeModelService.getAisle(authToken, location);
            printShelfList(aisle.getShelfList());
        } else {
            // Specific shelf is referenced
            Shelf shelf = this.storeModelService.getShelf(authToken, location);
            System.out.println(shelf);
        }
    }

    /**
     * Show details of a Store, including the id, name, address, current customers,
     * aisles, inventory, and devices (sensor/appliances).
     *
     * Expected command line to be formatted as:
     *      show show <store_id>
     *
     * An API call is made to the Store Model Service to retrieve the requested
     * store. Using the store object, if one exists, the store information is
     * printed and subsequent calls are made to access aisles, customers, and
     * devices, all formatted using helper print() methods.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param storeId                       The store id.
     * @throws StoreModelServiceException   If the call to getInventory() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the processCommand() method.
     */
    private void showStore(String authToken, String storeId)
            throws StoreModelServiceException {

        Store store = this.storeModelService.getStore(authToken, storeId);

        printSectionHeader("Store Info");
        System.out.println(store);

        printSectionHeader("Customers");
        List<Customer> customers = this.storeModelService.getStoreCustomers(authToken, storeId);
        printCustomerList(customers);

        printSectionHeader("Aisles");
        printAisleList(store.getAisleList());

        printSectionHeader("Devices");
        List<Device> devices = this.storeModelService.getStoreDevices(authToken, storeId);
        printDeviceList(devices);
    }

    private void update(String token, String command, String object, String id, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {

        switch (object.toLowerCase()) {
            case "inventory":
                updateInventory(token, command, id, args);
                break;
            case "customer":
                updateCustomer(token, command, id, args);
                break;
            default:
                throw new CommandProcessorException(command, "Unknown command");
        }

    }


    /**
     * Update the location of a customer.
     *
     *
     *
     * @param authToken
     * @param command
     * @param customerId
     * @param args
     * @throws CommandProcessorException
     */
    private void updateCustomer(String authToken, String command, String customerId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        String location = null;
        try {
            location = getArgument("location", args);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        }

        this.storeModelService.updateCustomer(authToken, customerId, location);
        return;
    }

    /**
     *
     * @param authToken
     * @param command
     * @param inventoryId
     * @param args
     * @throws CommandProcessorException
     */
    private void updateInventory(String authToken, String command, String inventoryId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        Integer updateCount = null;

        try {
            updateCount = Integer.parseInt(getArgument("update_count", args));
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Update count is an invalid number.");
        }

        this.storeModelService.updateInventory(authToken, inventoryId, updateCount);
        return;
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
            String description = getArgument("description", args);
            String seed = getArgument("seed", args);

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
            Integer amount = Integer.parseInt(getArgument("amount", args));
            Integer fee = Integer.parseInt(getArgument("fee", args));
            String payload = getArgument("payload", args);
            String payerAddress = getArgument("payer", args);
            String receiverAddress = getArgument("receiver", args);

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

    /**
     * Helper method to convert String into Enum type.
     * @param values
     * @param type
     * @return
     */
    private Enum getEnum(Enum[] values, String type) {
        if (type == null) {
            return null;
        }

        for(Enum toCheck : values) {
            if(type.equals(toCheck.toString().toLowerCase())) {
                return toCheck;
            }
        }

        return null;
    }

    private String[] parseLocationIdentifier(String location) {
        return location.split(":");
    }

    private Map<String, String> getStoreEntityInfo(String command, String[] keys, List<String> args)
            throws CommandProcessorException {
        // Store the keys and values for a Store entity
        Map<String, String> entityInfo = new HashMap<String, String>();
        try {
            for (String key : keys) {
                entityInfo.put(key, getArgument(key, args));
            }
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        }

        return entityInfo;
    }

    private void printCreatedEntity(String entity, String id) {
        String output = String.format("Defined new %s: '%s'.", entity, id);
        System.out.println(output);
    }
    private void printSectionHeader(String section) {

        String separator = "-----------------------";
        System.out.println(separator);
        System.out.println(section + ":");
        System.out.println(separator);
    }
}