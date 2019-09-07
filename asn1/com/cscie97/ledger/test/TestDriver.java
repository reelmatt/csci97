package com.cscie97.ledger.test;

import java.io.*;
import com.cscie97.ledger.*;

public class TestDriver {
    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("length is > 1, oops?");

        } else {
            try {
                CommandProcessor engine = new CommandProcessor();
                engine.processCommandFile(args[0]);
            } catch (CommandProcessorException e) {
                System.out.println("cmd processor exception, " + e);
            }

        }




    }

}