package com.cscie97.store.test;

import com.cscie97.store.controller.CommandProcessor;
import com.cscie97.store.controller.CommandProcessorException;

import com.cscie97.store.controller.StoreControllerService;
import com.cscie97.store.controller.StoreControllerServiceInterface;
import com.cscie97.store.model.StoreModelService;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.authentication.AuthenticationService;
import com.cscie97.store.authentication.AuthenticationServiceInterface;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;

/**
 * Main program to run test scripts for Ledger Service.
 *
 * Accepts a single command line argument, which is a command file. The
 * TestDriver then processes the commands using the CommandProcessor class.
 *
 * @author Matthew Thomas
 * @see CommandProcessor.java
 * @see ledger.script
 * @see errors.script
 */
public class TestDriver {
    public static void main(String[] args) {
        // Require only one command line argument (command file)
        if (args.length == 0 || args.length > 1) {
            System.err.println(
                "usage: java -cp . com.cscie97.store.test.TestDriver <test script file>"
            );
            return;
        }



        AuthenticationServiceInterface auth = AuthenticationService.getInstance();

        /** Ledger to manage accounts and process transactions. */
        Ledger ledger = null;
        try {
            ledger = new Ledger("test", "test ledger", "cambridge");
        } catch (LedgerException e) {
            // Failed to create Ledger. Cannot proceed further, so print error and return
            System.err.println(e);
            return;
        }

        /** StoreModelService to create, read, and update Store objects. */
        StoreModelServiceInterface model = new StoreModelService(auth);

        /** StoreControllerService to monitor store state and control appliances. */
        StoreControllerServiceInterface controller = new StoreControllerService(auth, model, ledger);

        // Process file
        try {
            CommandProcessor processor = new CommandProcessor(controller, model, auth, ledger);
            processor.processCommandFile(args[0]);
        } catch (CommandProcessorException e) {
            System.err.println(e);
        }

        return;
    }
}