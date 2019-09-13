package com.cscie97.ledger.test;

import java.io.*;
import com.cscie97.ledger.*;

public class TestDriver {
    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("length is > 1, oops?");
        } else {
            try {
                CommandProcessor processor = new CommandProcessor();
                processor.processCommandFile(args[0]);
            } catch (CommandProcessorException e) {
                System.err.println("cmd processor exception, " + e);
            }
        }

        return;
    }
}