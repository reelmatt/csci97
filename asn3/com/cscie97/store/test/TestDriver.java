package com.cscie97.store.test;

import com.cscie97.store.controller.CommandProcessor;
import com.cscie97.store.controller.CommandProcessorException;

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

        // Process file
        try {
            CommandProcessor processor = new CommandProcessor();
            processor.processCommandFile(args[0]);
        } catch (CommandProcessorException e) {
            System.err.println(e);
        }

        return;
    }
}