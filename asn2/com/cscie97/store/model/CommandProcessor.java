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
        // StoreModelService instance needs to be created to run commands
        if (this.storeModelService == null) {
            throw new CommandProcessorException("command", "StoreModelService has not been initialized.", lineNumber);
        }

        // Break command line into a list of arguments
        List<String> args = parseCommand(commandLine);

        // Key syntax variables
        String command = null;
        String storeObject = null;
        String id = null;
        String authToken = "authToken is part of Assignment 3";

        try {
            // First argument is the command to run
            command = args.remove(0);

            // Second argument is the object (Store, Aisle, etc.)
            storeObject = args.remove(0);

            // Third argument is the identifier (storeId, aisleId, etc.)
            id = args.remove(0);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(commandLine, "Missing arguments.", lineNumber);
        }

        // Pass remaining args into helper methods
        try {
            switch (command.toLowerCase()) {
                case "define":
                    define(authToken, command, storeObject, id, args);
                    break;
                case "show":
                    show(authToken, command, storeObject, id, args);
                    break;
                case "update":
                    update(authToken, command, storeObject, id, args);
                    break;
                case "create":
                    create(authToken, command, storeObject, id, args);
                    break;
                case "get":
                    getBasket(authToken, command, id, args);
                    break;
                case "add":
                    addItem(authToken, command, id, args);
                case "remove":
                    removeItem(authToken, command, id, args);
                case "clear":
                    clearBasket(authToken, command, id, args);
                    break;
                default:
                    throw new CommandProcessorException(command, "Unknown command", lineNumber);
            }
        } catch (StoreModelServiceException e) {
            System.err.println(e);
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
     * Helper function to define a specified Store entity.
     *
     * @param token
     * @param command
     * @param object
     * @param id
     * @param args
     * @throws CommandProcessorException
     * @throws StoreModelServiceException
     */
    private void define(String token,
                        String command,
                        String object,
                        String id,
                        List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        try {

            switch (object.toLowerCase()) {
                case "aisle":
                    defineAisle(token, command, id, args);
                    break;
                case "customer":
                    defineCustomer(token, command, id, args);
                    break;
                case "device":
                    defineDevice(token, command, id, args);
                    break;
                case "inventory":
                    defineInventory(token, command, id, args);
                    break;
                case "product":
                    defineProduct(token, command, id, args);
                    break;
                case "shelf":
                    defineShelf(token, command, id, args);
                    break;
                case "store":
                    defineStore(token, command, id, args);
                    break;
                default:
                    throw new CommandProcessorException(command, "Unknown command");
            }
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }
    }

    private void show(String token,
                      String command,
                      String object,
                      String id,
                      List<String> args)
            throws CommandProcessorException, StoreModelServiceException {

        String[] ids = parseLocationIdentifier(id);

        // Causes problems for Product and Customer, rethink placement
        Store store;
        try {
            switch (object.toLowerCase()) {
                case "aisle":
                    showAisle(token, command, id, args);
                    break;
                case "customer":
                    System.out.println(this.storeModelService.getCustomer("auth", id));
                    break;
                case "device":
                    System.out.println(this.storeModelService.getDevice("auth", id));
                    break;
                case "inventory":
                    showInventory(token, command, id, args);
                    break;
                case "product":
                    System.out.println(this.storeModelService.getProduct("auth", id));
                    break;
                case "shelf":
                    showShelf(token, command, id, args);
                    break;
                case "store":
                    showStore(token, command, id, args);
                    break;
                default:
                    throw new CommandProcessorException(command, "Unknown command");
            }

        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

    }

    private void update(String token,
                        String command,
                        String object,
                        String id,
                        List<String> args)
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

    private void create(String token,
                        String command,
                        String object,
                        String id,
                        List<String> args)
            throws CommandProcessorException, StoreModelServiceException {

        switch (object.toLowerCase()) {
            case "command":
                createCommand(token, command, id, args);
                break;
            case "event":
                createEvent(token, command, id, args);
                break;
            default:
                throw new CommandProcessorException(command, "Unknown command");
        }

    }

    /**
     * Helper method to convert String into Enum type.
     * @param values
     * @param type
     * @return
     */
    private Enum getEnum(Enum[] values, String type) {
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

    private Map<String, String> getStoreEntityInfo(String command,
                                                   String[] keys,
                                                   List<String> args) throws CommandProcessorException {
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

    /**
     * Define a new Aisle.
     *
     * Expected command line to be formatted as:
     *      define aisle <store_id>:<aisle_number>
     *             name <name>
     *             description <description>
     *             location (floor | store_room)
     *
     * @param authToken
     * @param command
     * @param aisleId
     * @param args
     * @throws CommandProcessorException
     */
    private void defineAisle(String authToken, String command, String aisleId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        // Get information needed to define an Aisle
        String[] keys = {"name", "description", "location"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        // Define Aisle
        Aisle aisle = this.storeModelService.defineAisle(
                authToken,
                aisleId,
                entityInfo.get("name"),
                entityInfo.get("description"),
                (Location) getEnum(Location.values(), entityInfo.get("location"))
        );

        printCreatedEntity("aisle", aisle.getId());
    }

    private void defineCustomer(String authToken, String command, String customerId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {

        String[] keys = {"first_name", "last_name", "type", "email_address", "account"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

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

    private void defineDevice(String authToken, String command, String deviceId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        String[] keys = {"name", "type", "location"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        Device device = this.storeModelService.defineDevice(
                authToken,
                deviceId,
                entityInfo.get("name"),
                entityInfo.get("type"),
                entityInfo.get("location")
        );

        printCreatedEntity("device", device.getId());
    }

    private void defineInventory(String authToken, String command, String inventoryId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {

        String[] keys = {"location", "capacity", "count", "product"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        Integer capacity, count = null;
        try {
            capacity = Integer.parseInt(entityInfo.get("capacity"));
            count = Integer.parseInt(entityInfo.get("count"));
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Argument is not a valid Integer.");
        }

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

    private void defineProduct(String authToken, String command, String productId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {
        String[] keys = {"name", "description", "size", "category", "unit_price", "temperature"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

        Integer unitPrice = null;

        try {
            unitPrice = Integer.parseInt(entityInfo.get("unit_price"));
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Unit price is not a valid Integer.");
        }

        Product product = this.storeModelService.defineProduct(
                authToken,
                productId,
                entityInfo.get("name"),
                entityInfo.get("description"),
                entityInfo.get("size"),
                entityInfo.get("category"),
                unitPrice,
                (Temperature) getEnum(Temperature.values(), entityInfo.get("temperature"))
        );

        printCreatedEntity("product", product.getId());
    }


    private void defineShelf(String authToken, String command, String shelfId, List<String> args)
            throws CommandProcessorException, StoreModelServiceException {

        String[] keys = {"name", "description", "level", "temperature"};
        Map<String, String> entityInfo = getStoreEntityInfo(command, keys, args);

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
     * Additional arguments provided are ignored. If a piece of information is
     * not provided, 'null' will be passed through to the StoreModelService. If
     * the information is required, a StoreModelException will be returned.
     *
     * @param authToken                     Authentication token to validate with the service.
     * @param command                       The command to be run (used for format Exception).
     * @param storeId                       The store ID to use.
     * @param args                          Command line arguments.
     * @throws CommandProcessorException    Exception is thrown via call to getStoreEntityInfo()
     *                                      with an IndexOutOfBoundsException indicating missing
     *                                      arguments.
     *         StoreModelServiceException   If the call to defineStore() fails, the Model Service
     *                                      will throw an Exception, which is handled and printed
     *                                      in the define() helper method.
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





    private void showAisle(String authToken,
                           String command,
                           String location,
                           List<String> args) throws CommandProcessorException, StoreModelServiceException {
        // Parse the location String
        String[] ids = parseLocationIdentifier(location);

        // Only a store ID
        if (ids.length == 0) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } else if (ids.length == 1) {
            Store store = this.storeModelService.getStore(authToken, location);
            store.getAisleList().forEach((aisle) -> System.out.println(aisle));
        } else {
            Aisle aisle = this.storeModelService.getAisle(authToken, location);
            System.out.println(aisle);
        }
    }



    private void showInventory(String authToken,
                           String command,
                           String location,
                           List<String> args) throws CommandProcessorException, StoreModelServiceException {
        // Parse the location String
        String[] ids = parseLocationIdentifier(location);

        // Only a store ID
        if (ids.length == 0) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } else if (ids.length == 1) {
            Store store = this.storeModelService.getStore(authToken, location);
            store.getAisleList().forEach((aisle) -> System.out.println(aisle));
        } else if (ids.length == 2) {
            Aisle aisle = this.storeModelService.getAisle(authToken, location);
            aisle.getShelfList().forEach((shelf) -> System.out.println(shelf));
        } else if (ids.length == 3) {
            Shelf shelf = this.storeModelService.getShelf(authToken, location);
            shelf.getInventoryList().forEach((inventory) -> System.out.println(inventory));
        } else {
            Inventory inventory = this.storeModelService.getInventory(authToken, location);
            System.out.println(inventory);
        }
    }

    private void showShelf(String authToken,
                           String command,
                           String location,
                           List<String> args) throws CommandProcessorException, StoreModelServiceException {
        // Parse the location String
        String[] ids = parseLocationIdentifier(location);

        // Only a store ID
        if (ids.length == 0) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } else if (ids.length == 1) {
            Store store = this.storeModelService.getStore(authToken, location);
            store.getAisleList().forEach((aisle) -> System.out.println(aisle));
        } else if (ids.length == 2) {
            Aisle aisle = this.storeModelService.getAisle(authToken, location);
            aisle.getShelfList().forEach((shelf) -> System.out.println(shelf));
        } else {
            Shelf shelf = this.storeModelService.getShelf(authToken, location);
            System.out.println(shelf);
        }
    }

    private void showStore(String authToken,
                           String command,
                           String storeId,
                           List<String> args) throws CommandProcessorException, StoreModelServiceException {

        Store store = this.storeModelService.getStore(authToken, storeId);

        printSectionHeader("Store Info");
        System.out.println(store);

        printSectionHeader("Customers");
        List<Customer> customers = this.storeModelService.getStoreCustomers(authToken, storeId);
//            printList("customers", customers);
        if (customers.size() == 0) {
            System.out.println("The store has 0 customers.");
        } else {
            customers.forEach((customer) -> System.out.println(customer));
        }

        printSectionHeader("Aisles");
        List<Aisle> aisles = store.getAisleList();
//            printList("aisles", aisles);
        if (aisles.size() == 0) {
            System.out.println("The store has 0 aisles.");
        } else {
            aisles.forEach((aisle) -> System.out.println(aisle));
        }

        printSectionHeader("Devices");
        List<Device> devices = this.storeModelService.getStoreDevices(authToken, storeId);
//            printList("devices", devices);
        if (devices.size() == 0) {
            System.out.println("The store has 0 devices.");
        } else {
            devices.forEach((device) -> System.out.println(device));
        }
    }

    //    private void printList(String entityName, List<T> entities) {
//        if (entities.size() == 0) {
//            System.out.println("The store has 0 " + entityName + ".");
//        } else {
//            entities.forEach((entity) -> System.out.println(entity));
//        }
//    }

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
    private void updateCustomer(String authToken,
                                String command,
                                String customerId,
                                List<String> args) throws CommandProcessorException {
        try {
            String location = getArgument("location", args);
            this.storeModelService.updateCustomer(authToken, customerId, location);

        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

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
    private void updateInventory(String authToken,
                                 String command,
                                 String inventoryId,
                                 List<String> args) throws CommandProcessorException {
        try {
            String updateCount = getArgument("update_count", args);
            this.storeModelService.updateInventory(authToken, inventoryId, Integer.parseInt(updateCount));

        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Update count is an invalid number.");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }


    }



    private void createCommand(String authToken,
                               String command,
                               String deviceId,
                               List<String> args) throws CommandProcessorException {
        try {
            String message = getArgument("message", args);
            this.storeModelService.createCommand(authToken, deviceId, message);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }
    }

    private void createEvent (String authToken,
                              String command,
                              String deviceId,
                              List<String> args) throws CommandProcessorException {
        try {
            String event = getArgument("event", args);
            this.storeModelService.createEvent(authToken, deviceId, event);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }
    }

    private void getBasket (String authToken,
                            String command,
                            String customerId,
                            List<String> args) throws CommandProcessorException {
        try {
            Basket basket = this.storeModelService.getCustomerBasket(authToken, customerId);

            if (basket == null) {
                System.out.println("Customer '" + customerId + "' does not have a basket. Creating one.");
                basket = this.storeModelService.createCustomerBasket(authToken, customerId);
            }
            System.out.println("Basket created for Customer '" + customerId + "'");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }
    }

    private void addItem (String authToken,
                          String command,
                          String basketId,
                          List<String> args) throws CommandProcessorException {
        try {
            String productId = getArgument("product", args);
            Integer itemCount = Integer.parseInt(getArgument("item_count", args));

            this.storeModelService.addItemToBasket(authToken, basketId, productId, itemCount);

        } catch (IndexOutOfBoundsException e) {
            throw new CommandProcessorException(command, "Missing arguments.");
        } catch (NumberFormatException e) {
            throw new CommandProcessorException(command, "Invalid item count.");
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }
    }

    private void removeItem (String authToken,
                             String command,
                             String basketId,
                             List<String> args) throws CommandProcessorException {

    }

    private void clearBasket (String authToken,
                              String command,
                              String basketId,
                              List<String> args) throws CommandProcessorException {

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