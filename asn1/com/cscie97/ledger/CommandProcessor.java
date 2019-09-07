package com.cscie97.ledger;

import java.io.*;

public class CommandProcessor {
    public void processCommand(String command) {
        return;
    }

    public static void processCommandFile(String file) throws CommandProcessorException {
        System.out.println("file is " + file);
        String currentLine;

        try {
            FileReader testScript = new FileReader(file);

            BufferedReader reader = new BufferedReader(testScript);


            while ( (currentLine = reader.readLine()) != null ) {
                if (currentLine.length() == 0 || currentLine.charAt(0) == '#') {
//                        System.out.println("Skip line");
                } else {
                    System.out.println(currentLine);
                }


            }

            System.out.println("Reached EOF, closing file....");
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.out.println(e);
            throw new CommandProcessorException();
        } catch (IOException e) {
            System.out.println("oops, IO exception");
            System.out.println(e);
            throw new CommandProcessorException();
        }
    }

}