package com.cscie97.ledger;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// Regex pattern and matching found in this StackOverflow post
// https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double?rq=1
public class CommandProcessor {

    private Ledger ledger = null;

    public void processCommand(String commandLine) {
        System.out.println("\n" + commandLine);
        List<String> args = parseCommand(commandLine);

//        System.out.println("\targs IN COMMAND: " + args.toString());
        String command = args.remove(0);
        switch (command) {
            case "create-ledger":
                createLedger(args);
                break;
            case "create-account":
                createAccount(args);
                break;
            case "get-account-balance":
                getAccountBalance(args);
                break;
            default:
                System.out.println("Other command is " + command);
                break;
        }
        return;
    }

    public void processCommandFile(String file) throws CommandProcessorException {
        System.out.println("file is " + file);
        String currentLine = "";

        try {
            FileReader testScript = new FileReader(file);
            BufferedReader reader = new BufferedReader(testScript);

            while ( (currentLine = reader.readLine()) != null ) {
                if (currentLine.length() > 0 && currentLine.charAt(0) != '#') {
                     processCommand(currentLine);
                }
            }

            System.out.println("Reached EOF, closing file....");
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.out.println(e);
            throw new CommandProcessorException(currentLine, "file not found", 23);
        } catch (IOException e) {
            System.out.println("oops, IO exception");
            System.out.println(e);
            throw new CommandProcessorException(currentLine, "file not found", 23);
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
//        System.out.println("\targs IN COMMAND: " + args.toString());

        if (this.ledger != null) {
            String account = this.ledger.createAccount(args.get(0));
            Account newAccount = new Account(account);
            System.out.println("Created account... " + newAccount);
        }
        return;
    }

    private void createLedger(List<String> args) {
//        System.out.println("\targs IN COMMAND: " + args.toString());
        this.ledger = new Ledger(args.get(0), args.get(1), args.get(2));

        System.out.println("Created ledger, name is... " + this.ledger.toString());
        System.out.println("The ledger currently has " + this.ledger.numberOfBlocks() + " blocks.");
        return;
    }

    private void processTransaction() {

    }

    private void getAccountBalance(List<String> args) {
        System.out.println(args.get(0) + " has an account balance of " + this.ledger.getAccountBalance(args.get(0)));
        return;
    }

    private void getBlock() {

    }

    private void getTransaction() {

    }

    private void validate() {

    }
}