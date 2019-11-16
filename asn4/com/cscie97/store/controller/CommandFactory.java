package com.cscie97.store.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.model.Aisle;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Inventory;
import com.cscie97.store.model.Observer;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.Shelf;
import com.cscie97.store.model.StoreModelService;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.authentication.AuthenticationServiceInterface;
import com.cscie97.store.authentication.AuthenticationException;
import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.InvalidAuthTokenException;
import com.cscie97.store.authentication.AuthToken;

/**
 * A CommandFactory works in conjuction with a StoreModelServiceInterface and a
 * Ledger to parse Store events and generate Commands to run sets of actions. The
 * Factory is run in a StoreControllerService and is invoked anytime the Observer
 * is notified of an event by the Subject (StoreModelServiceInterface).
 *
 * @author Matthew Thomas
 */
public class CommandFactory {
    /** The StoreModelService that provides API to update state. */
    private StoreModelServiceInterface storeModel;

    /** The AuthenticationService to verify restricted access. */
    private AuthenticationServiceInterface authService;

    /** The Ledger which processes transactions. */
    private Ledger ledger;

    /**
     * CommandFactory Constructor
     *
     * Creates an instance of the Command Factory to parse events and create
     * corresponding Commands to be executed by a Store Controller.
     *
     * @param storeModel    The StoreModelService that provides API to update state.
     * @param ledger        The Ledger which processes transactions.
     */
    public CommandFactory(StoreModelServiceInterface storeModel, AuthenticationServiceInterface authService, Ledger ledger) {
        this.storeModel = storeModel;
        this.ledger = ledger;
        this.authService = authService;
    }

    /**
     * Parse an event and create a corresponding Command.
     *
     * The event is parsed and checked against a list of known 'eventCommands'.
     * If recognized, a helper method is called that handles retrieving necessary
     * store objects from event parameters.
     *
     * @param   authToken                       Token to authenticate with StoreModel API.
     * @param   event                           Event message created by a store Device.
     * @param   device                          The Device which detected the event.
     * @return                                  The Command to be executed.
     * @throws  StoreControllerServiceException If a Command is not recognized, or a
     *                                          different Exception is thrown while creating
     *                                          the Command (such as missing parameters).
     */
    public Command createCommand(AuthToken token, String event, Device device)
            throws StoreControllerServiceException {
        // Break stimulus into a list of arguments
        List<String> eventArgs = parseCommand(event);

        // Key syntax variables
        String userId = null;
        String credential = null;
        String eventCommand = null;
        String id = null;

        try {
            // First argument is the user id
            userId = eventArgs.remove(0);

            // First argument is the auth credential
            credential = eventArgs.remove(0);

            // Second argument is the keyword of the stimulus
            eventCommand = eventArgs.remove(0);

            // Third argument is often an id (customer, product)
            // Natural language commands check full event for substring, so OK
            // to remove from parse list
            id = eventArgs.remove(0);
        } catch (IndexOutOfBoundsException e) {
            throw new StoreControllerServiceException(event, "Missing arguments.");
        }

        // Check user Credential and retireve authToken
        AuthToken authToken;
        try {
            authToken = this.authService.login(userId, credential);
        } catch (AuthenticationException e) {
            System.err.println(e);
            return null;
        } catch (AccessDeniedException e) {
            System.err.println(e);
            return null;
        }


        System.out.println("FACTORY: token == " + authToken.toString());
        // Command to create
        Command storeCommand = null;

        // Check stimulus for known events
        try {
            switch (eventCommand.toLowerCase()) {
                case "customer":
                    storeCommand = createCustomerCommand(
                        authToken,
                        this.storeModel,
                        this.ledger,
                        device,
                        event,
                        eventArgs,
                        id
                    );
                    break;
                case "emergency":
                    storeCommand = createEmergencyCommand(
                        authToken,
                        this.storeModel,
                        device,
                        eventArgs,
                        id
                    );
                    break;
                case "product":
                    storeCommand = createProductCommand(
                        authToken,
                        this.storeModel,
                        device,
                        event,
                        eventArgs,
                        id
                    );
                    break;
                case "can":
                case "sound":
                    // check for natural language requests via microphones
                    // requests start with 'can' or 'sound'
                    storeCommand = createMicrophoneCommand(
                        authToken,
                        storeModel,
                        device,
                        event,
                        eventArgs
                    );
                    break;
                default:
                    throw new StoreControllerServiceException(event, "Unknown event");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new StoreControllerServiceException(event, "Missing arguments.");
        }

        return storeCommand;
    }


    /**
     * Creates a Command referring to a Customer.
     *
     * All Customer commands contain a customer in the event message. The
     * corresponding Customer object is retrieved from the Store Model Service
     * and the remaining event message is parsed.
     *
     * Customer requests, including the keyword 'says', are handled by the
     * createCustomerRequestCommand() method. The following Commands are
     * recognized by this method:
     *
     * BasketEventCommand which has an event string formatted as:
     *      customer <customer> (adds | removes) <product> from <aisle>:<shelf>
     *
     * CustomerSeenCommand
     *      customer <customer> enters <aisle>
     *
     * AssistCustomerCommand
     *      customer <customer> assistance
     *
     * CheckoutCommand
     *      customer <customer> approaches turnstile
     *
     * EnterStoreCommand
     *      customer <customer> waiting to enter
     *
     *
     * @param   authToken                       Token to authenticate with StoreModel API
     * @param   storeModel                      StoreModel to get/update state.
     * @param   ledger                          Ledger to use for transactions.
     * @param   device                          The Device which detected the event.
     * @param   event                           The full event message emitted by the Device.
     * @param   eventArgs                       The remaining arguments in the parsed event.
     * @param   customerId                      The Customer id.
     * @return                                  The Command to be executed.
     * @throws StoreControllerServiceException  If parameters are missing in the event, or
     *                                          an exception with retrieving Store Model objects.
     */
    private Command createCustomerCommand(AuthToken authToken,
                                         StoreModelServiceInterface storeModel,
                                         Ledger ledger,
                                         Device device,
                                         String event,
                                         List<String> eventArgs,
                                         String customerId)
            throws StoreControllerServiceException {
        try {
            // Retrieve the Customer
            Customer customer = this.storeModel.getCustomer(authToken, customerId);

            // Extract the next keyword from the event message
            String action = eventArgs.remove(0);

            // Check against known events
            switch (action.toLowerCase()) {
                case "adds":
                case "removes":
                    // Retrieve the Product
                    Product product = this.storeModel.getProduct(authToken, eventArgs.get(0));

                    // Event contains <aisle>:<shelf> id, the <store> id needs to be prepended
                    String locationId = device.getStore() + ":" + eventArgs.get(2);
                    Shelf shelf = this.storeModel.getShelf(authToken, locationId);
                    return new BasketEventCommand(authToken, storeModel, device, customer, product, shelf, action, locationId);
                case "enters":
                    // Construct fully qualified aisle location
                    String aisleLocation = device.getStore() + ":" + eventArgs.get(0);
                    Aisle aisle = this.storeModel.getAisle(authToken, aisleLocation);
                    return new CustomerSeenCommand(authToken, storeModel, device, customer, aisle);
                case "says":
                    return createCustomerRequestCommand(authToken, storeModel, device, event, eventArgs, customer);
                case "assistance":
                    return new AssistCustomerCommand(authToken, storeModel, device, customer);
                case "approaches":
                    return new CheckoutCommand(authToken, storeModel, device, this.ledger,  customer);
                case "waiting":
                    return new EnterStoreCommand(authToken, storeModel, device, this.ledger, customer);
                default:
                    throw new StoreControllerServiceException(event, "Unknown event");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new StoreControllerServiceException(event, "Missing event parameters.");
        } catch (StoreModelServiceException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (AccessDeniedException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (AuthenticationException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (InvalidAuthTokenException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        }
    }

    /**
     * Creates a Command referring to a Customer request.
     *
     * There are two customer request commands, CheckAccountBalanceCommand and
     * FetchProductCommand. A Customer is obtained via the createCustomerCommand()
     * method and passed to this createCustomerRequestCommand() method for all events
     * that include "says" in the message.
     *
     * CheckAccountBalanceCommand has an expected event string formatted as:
     *      customer <customer> says 'what is the total basket value'
     *
     * FetchProductCommand has an expected event string formatted as:
     *      customer <customer> says please get me <number> of <product>
     *
     * @param   authToken                       Token to authenticate with StoreModel API
     * @param   storeModel                      StoreModel to get/update state.
     * @param   device                          The Device which detected the event.
     * @param   event                           The full event message emitted by the Device.
     * @param   eventArgs                       The remaining arguments in the parsed event.
     * @param   customer                        The Customer object referenced in the request.
     * @return                                  The Command to be executed.
     * @throws StoreControllerServiceException  If parameters are missing in the event, or
     *                                          an exception with retrieving Store Model objects.
     */
    private Command createCustomerRequestCommand(AuthToken authToken,
                                                 StoreModelServiceInterface storeModel,
                                                 Device device,
                                                 String event,
                                                 List<String> eventArgs,
                                                 Customer customer)
            throws StoreControllerServiceException {
        try {
            if (event.toLowerCase().contains("what is the total basket value")) {
                return new CheckAccountBalanceCommand(authToken, storeModel, device, this.ledger, customer);
            } else if (event.toLowerCase().contains("please get me ")) {
//                System.out.println("FETCH PRODUCT");
//                eventArgs.forEach(arg -> System.out.println(arg));

                // Retrieve the amount
                Integer amount = Integer.parseInt(eventArgs.get(3));

                // Retrieve the Product
                Product product = this.storeModel.getProduct(authToken, eventArgs.get(5));

                // Retrieve location
                String location = eventArgs.get(7);
                return new FetchProductCommand(authToken, storeModel, device, customer, amount, product, location);
            } else {
                throw new StoreControllerServiceException(event, "Unknown event");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new StoreControllerServiceException(event, "Missing event parameters.");
        } catch (NumberFormatException e) {
            throw new StoreControllerServiceException(event, "Amount must be a valid Integer.");
        } catch (StoreModelServiceException e) {
            throw new StoreControllerServiceException(event, e.toString());
        } catch (AccessDeniedException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (AuthenticationException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (InvalidAuthTokenException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        }
    }

    /**
     * Create EmergencyCommand.
     *
     * Expected event string to be formatted as:
     *      emergency <emergency> in <aisle>
     *
     * The <emergency> type is extracted in the createCommand() method. This
     * method retrieve the <aisle> id from the remaining eventArgs and assembles
     * the fully qualified aisle ID (of form <store>:<aisle>) to retrieve the
     * Aisle object from the Store Model.
     *
     * @param   authToken                       Token to authenticate with StoreModel API
     * @param   storeModel                      StoreModel to get/update state.
     * @param   device                          The Device which detected the event.
     * @param   eventArgs                       The remaining arguments in the parsed event.
     * @param   emergency                       The type of emergency reported.
     * @return                                  The Command to be executed.
     * @throws StoreControllerServiceException  If parameters are missing in the event, or
     *                                          an exception with retrieving Store Model objects.
     */
    private Command createEmergencyCommand(AuthToken authToken,
                                           StoreModelServiceInterface storeModel,
                                           Device device,
                                           List<String> eventArgs,
                                           String emergency)
            throws StoreControllerServiceException {
        try {
            // Construct fully qualified aisle location
            String aisleLocation = device.getStore() + ":" + eventArgs.get(1);
            Aisle aisle = this.storeModel.getAisle(authToken, aisleLocation);
            return new EmergencyCommand(authToken, this.storeModel, device, emergency, aisle);
        } catch (IndexOutOfBoundsException e) {
            throw new StoreControllerServiceException("emergency event", "Missing aisle location.");
        } catch (StoreModelServiceException e) {
            throw new StoreControllerServiceException("emergency event", e.getReason());
        } catch (AccessDeniedException e) {
            throw new StoreControllerServiceException("emergency event", e.getReason());
        } catch (AuthenticationException e) {
            throw new StoreControllerServiceException("emergency event", e.getReason());
        } catch (InvalidAuthTokenException e) {
            throw new StoreControllerServiceException("emergency event", e.getReason());
        }
    }

    /**
     * Creates a Command referring to a Microphone request.
     *
     * There are two 'natural language' requests emitted by a microphone, the
     * BrokenGlassCommand and MissingPersonCommand.
     *
     * BrokenGlassCommand has an expected event string formatted as:
     *      sound of breaking glass in <aisle>
     *
     * If a broken glass event, 'sound' and 'of' are removed from the eventArgs
     * during parsing, leaving the aisle ID at index 3. The fully qualified
     * aisle location (of form <store>:<aisle>) is constructed to retrieve the
     * Aisle object from the Store Model.
     *
     * MissingPersonCommand has an expected event string formatted as:
     *      can you help me find <customer name>
     *
     * If a missing person event, 'can' and 'you' are removed from the eventArgs
     * during parsing, leaving the customer name at index 3. The Command accepts
     * a customer name as either their ID, or full name structure as "first last".
     *
     * @param   authToken                       Token to authenticate with StoreModel API
     * @param   storeModel                      StoreModel to get/update state.
     * @param   device                          The Device which detected the event.
     * @param   event                           The full event message emitted by the Device.
     * @param   eventArgs                       The remaining arguments in the parsed event.
     * @return                                  The Command to be executed.
     * @throws StoreControllerServiceException  If parameters are missing in the event, or
     *                                          an exception with retrieving Store Model objects.
     */
    private Command createMicrophoneCommand(AuthToken authToken,
                                            StoreModelServiceInterface storeModel,
                                            Device device,
                                            String event,
                                            List<String> eventArgs)
            throws StoreControllerServiceException {
        try {
            if (event.toLowerCase().contains("sound of breaking glass")) {
                // Construct fully qualified aisle location
                String aisleLocation = device.getStore() + ":" + eventArgs.get(3);
                Aisle aisle = this.storeModel.getAisle(authToken, aisleLocation);
                return new BrokenGlassCommand(authToken, storeModel, device, aisle);
            } else if (event.toLowerCase().contains("can you help me find")) {
                String customerName = eventArgs.get(3);
                return new MissingPersonCommand(authToken, storeModel, device, customerName);
            } else {
                throw new StoreControllerServiceException(event, "Unknown command.");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new StoreControllerServiceException(event, "Missing event parameters.");
        } catch (StoreModelServiceException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (AccessDeniedException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (AuthenticationException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (InvalidAuthTokenException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        }
    }

    /**
     * Creates a Command referring to a Product.
     *
     * There are two Product commands, the CleaningCommand and RestockCommand.
     *
     * CleaningCommand has an expected event string formatted as:
     *      product <product> on floor <store>:<aisle>
     *
     * RestockCommand has an expected event string formatted as:
     *      product <product> inventory <inventory> restock
     *
     * The <product> id for both Commands is extracted in the createCommand()
     * method. The CleaningCommand event contains the fully qualified Aisle ID
     * and the RestockCommand contains an <inventory> id, that are extracted
     * and have their corresponding Store objects passed in to the Command
     * constructor.
     *
     * @param   authToken                       Token to authenticate with StoreModel API
     * @param   storeModel                      StoreModel to get/update state.
     * @param   device                          The Device which detected the event.
     * @param   event                           The full event message emitted by the Device.
     * @param   eventArgs                       The remaining arguments in the parsed event.
     * @param   productId                       The Product id.
     * @return                                  The Command to be executed.
     * @throws StoreControllerServiceException  If parameters are missing in the event, or
     *                                          an exception with retrieving Store Model objects.
     */
    private Command createProductCommand(AuthToken authToken,
                                          StoreModelServiceInterface storeModel,
                                          Device device,
                                          String event,
                                          List<String> eventArgs,
                                          String productId)
            throws StoreControllerServiceException {
        try {
            // Retrieve the product referenced
            Product product = this.storeModel.getProduct(authToken, productId);

            if (event.toLowerCase().contains("on floor")) {
                // Retrieve the Aisle referenced
                Aisle aisle = this.storeModel.getAisle(authToken, eventArgs.get(2));
                return new CleaningCommand(authToken, storeModel, device, product, aisle);
            } else if (event.toLowerCase().contains("restock")) {
                // Retrieve the Inventory location
                String inventoryLocation = eventArgs.get(1);

                // Retrieve the Inventory object
                Inventory inventory = this.storeModel.getInventory(authToken, inventoryLocation);
                return new RestockCommand(authToken, storeModel, device, product, inventory, inventoryLocation);
            } else {
                throw new StoreControllerServiceException(event, "Unknown command.");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new StoreControllerServiceException(event, "Missing event parameters.");
        } catch (StoreModelServiceException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (AccessDeniedException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (AuthenticationException e) {
            throw new StoreControllerServiceException(event, e.getReason());
        } catch (InvalidAuthTokenException e) {
            throw new StoreControllerServiceException(event, e.getReason());
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
}