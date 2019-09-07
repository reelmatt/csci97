package com.cscie97.ledger.test;

import java.io.*;
import com.cscie97.ledger.*;

public class TestDriver {
    public static void main(String[] args) {
        System.out.println("Hello world.");
        System.out.println("arg length is " + args.length);
        if (args.length > 1) {
            System.out.println("length is > 1, oops?");

        } else {
            try {
                CommandProcessor.processCommandFile(args[0]);
            } catch (CommandProcessorException e) {
                System.out.println("cmd processor exception, " + e);
            }

        }




    }

}