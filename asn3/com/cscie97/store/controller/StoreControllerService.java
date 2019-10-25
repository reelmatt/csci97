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
import com.cscie97.store.model.StoreModelServiceInterface;

/**
 * MissingPersonCommand.
 *
 * @author Matthew Thomas
 */
public class StoreControllerService implements StoreControllerServiceInterface, Observer {
    private StoreModelServiceInterface storeModel;
    private Ledger ledger;

    public StoreControllerService() {
        this.storeModel = new StoreModelService();

        try {
            this.ledger = new Ledger("test", "test ledger", "cambridge");
        } catch (LedgerException e) {
            System.err.println(e);
        }

        this.storeModel.register(this);
    }

    public void update(Device device, String event) {
        List<String> eventArgs = parseCommand(event);
        System.out.println("NOTIFICATION: " + eventArgs);

        Command storeCommand = null;

        switch (eventArgs.get(0).toLowerCase()) {
            case "customer":
                break;
            case "emergency":
                storeCommand = new EmergencyCommand(device);
                break;
            case "product":
                storeCommand = new CleaningCommand(device);
                break;
            case "can":
            case "sound":
                storeCommand = checkMicrophoneEvent(device, event, eventArgs);
                break;
            default:
                System.out.println("UNKNOWN event");
                break;
        }

        if (storeCommand != null) {
            storeCommand.execute();
        }

        return;
    }

    public Command createCommand(String event) {
        Device device = new Device("a", "b", "c");
        Command test = new CustomerSeenCommand(device);
        return test;
    }


    private Command checkMicrophoneEvent(Device device, String event, List<String> eventArgs) throws StoreControllerServiceException {
        if (eventArgs.size() < 5) {
            throw new StoreControllerServiceException("mic event", "Not enough arguments.");
        }
        Command command = null;

        if (event.toLowerCase().contains("can you help me find")) {
            command = new MissingPersonCommand(device);
        } else if (event.toLowerCase().contains("sound of breaking glass")) {
            command = new BrokenGlassCommand(device);
        } else {
            throw new StoreControllerService("mic event", "Unknown command.");
        }
    }



    public StoreModelServiceInterface getStoreModel() {
        return this.storeModel;
    }

    public Ledger getLedger() {
        return this.ledger;
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