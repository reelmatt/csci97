package com.cscie97.store.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.cscie97.store.model.Observer;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.model.StoreModelService;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.StoreModelServiceInterface;

/**
 * CommandFactory.
 *
 * @author Matthew Thomas
 */
public class CommandFactory {
    private StoreModelServiceInterface storeModel;
    private Ledger ledger;

    public CommandFactory(StoreModelServiceInterface storeModel, Ledger ledger) {
        // Init
        this.storeModel = storeModel;
        this.ledger = ledger;
    }


    public Command createCommand(String authToken, String event, Device device) throws StoreControllerServiceException {
        // Break stimulus into a list of arguments
        List<String> eventArgs = parseCommand(event);

        // Key syntax variables
        String eventCommand = null;
        String id = null;

        try {
            // First argument is the keyword of the stimulus
            eventCommand = eventArgs.remove(0);

            // Second argument is often an id (customer, product)
            // Natural language commands check full event for substring, so OK to remove from parse list
            id = eventArgs.remove(0);
        } catch (IndexOutOfBoundsException e) {
            throw new StoreControllerServiceException(event, "Missing arguments.");
        }

        // Command to create
        Command storeCommand = null;

        // Check stimulus for known events
        try {
            switch (eventCommand.toLowerCase()) {
                case "customer":
                    storeCommand = createCustomerCommand(authToken, this.storeModel, this.ledger, device, event, eventArgs, id);
                    break;
                case "emergency":
                    storeCommand = createEmergencyCommand(authToken, this.storeModel, device, eventArgs, id);
                    break;
                case "product":
                    storeCommand = createCleaningCommand(authToken, this.storeModel, device, eventArgs, id);
                    break;
                case "can":
                case "sound":
                    // check for natural language requests via microphones
                    storeCommand = createMicrophoneCommand(authToken, storeModel, device, event, eventArgs);
                    break;
                default:
                    throw new StoreControllerServiceException(event, "Unknown event");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new StoreControllerServiceException(event, "Missing arguments.");
        } catch (StoreModelServiceException e) {
            // Store objects do not exist...
            System.err.println(e);
        } catch(StoreControllerServiceException e) {
            System.err.println(e);
        }

        return storeCommand;
    }


    private Command createEmergencyCommand(String authToken,
                                                StoreModelServiceInterface storeModel,
                                                Device device,
                                                List<String> eventArgs,
                                                String emergency)
            throws StoreControllerServiceException, StoreModelServiceException {
        Aisle aisle = this.storeModel.getAisle(authToken, device.getStore() + ":" + eventArgs.get(1));
        return new EmergencyCommand(authToken, this.storeModel, device, emergency, aisle);
    }

    private Command createCleaningCommand(String authToken,
                                           StoreModelServiceInterface storeModel,
                                           Device device,
                                           List<String> eventArgs,
                                           String productId)
            throws StoreControllerServiceException, StoreModelServiceException {
        Product product = this.storeModel.getProduct(authToken, productId);
        Aisle aisle = this.storeModel.getAisle(authToken, eventArgs.get(2));
        return new CleaningCommand(authToken, storeModel, device, product, aisle);
    }

    private Command createCustomerCommand(String authToken,
                                         StoreModelServiceInterface storeModel,
                                         Ledger ledger,
                                         Device device,
                                         String event,
                                         List<String> eventArgs,
                                         String customerId) throws StoreControllerServiceException {
        Command command = null;

        String action;

        try {
            action = eventArgs.remove(0);

            Aisle aisle;
            Customer customer = this.storeModel.getCustomer(authToken, customerId);

            switch (action.toLowerCase()) {
                case "adds":
                case "removes":
                    command = new BasketEventCommand(authToken, storeModel, device);
                    break;
                case "enters":
                    aisle = this.storeModel.getAisle(authToken, device.getStore() + ":" + eventArgs.get(0));
                    command = new CustomerSeenCommand(authToken, storeModel, device, customer, aisle);
                    break;
                case "says":
                    command = createCustomerRequestCommand(authToken, storeModel, device, event, eventArgs, customer);
                    break;
                case "approaches":
                    command = new CheckoutCommand(authToken, storeModel, device);
                    break;
                case "waiting":
                    command = new EnterStoreCommand(device, storeModel, ledger, customerId);
                    break;
                default:
                    throw new StoreControllerServiceException(event, "Unknown event");
            }
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }
        return command;
    }

    private Command createCustomerRequestCommand(String authToken,
                                                 StoreModelServiceInterface storeModel,
                                                 Device device,
                                                 String event,
                                                 List<String> eventArgs,
                                                 Customer customer)
            throws StoreControllerServiceException, StoreModelServiceException {
        Command command = null;

        if (event.toLowerCase().contains("please get me ")) {
            Integer amount = Integer.parseInt(eventArgs.get(3));
            Product product = this.storeModel.getProduct(authToken, eventArgs.get(5));
            command = new FetchProductCommand(authToken, storeModel, device, customer, amount, product);
        } else if (event.toLowerCase().contains("what is the total basket value")) {
            command = new CheckAccountBalanceCommand(authToken, storeModel, device, this.ledger, customer);
        } else {
            throw new StoreControllerServiceException(event, "Unknown event");
        }

        return command;
    }


    private Command createMicrophoneCommand(String authToken,
                                            StoreModelServiceInterface storeModel,
                                            Device device,
                                            String event,
                                            List<String> eventArgs)
            throws StoreControllerServiceException, StoreModelServiceException {
        Command command = null;

        if (event.toLowerCase().contains("can you help me find")) {
            command = new MissingPersonCommand(authToken, storeModel, device);
        } else if (event.toLowerCase().contains("sound of breaking glass")) {
            Aisle aisle = this.storeModel.getAisle(authToken, device.getStore() + ":" + eventArgs.get(3));
            command = new BrokenGlassCommand(authToken, storeModel, device, aisle);
        } else {
            throw new StoreControllerServiceException("mic event", "Unknown command.");
        }

        return command;
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

}