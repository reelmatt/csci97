# Submission:
As per the assignment handout, my assignment 1 submission includes the source
code, data files, test results, and this document describing the results of my
implementation.

All files are contained within the zip file “matthew_thomas_assignment1.zip”.
Source code is found below the “com” directory, according to the package
specifications. As per the handout, the program can be compiled using the
command

    javac com/cscie97/ledger/*.java com/cscie97/ledger/test/*.java

when in the root level of the assignment directory. Test files are located at
the root level, and can be run using the following command, as per the handout:

    java -cp . com.cscie97.ledger.test.TestDriver <test file name>

where `<test file name>` is the name of the test script file. My test run
outputs can also be found in the root level, with “.out” appended to the test
file’s name. As per post @67 on Piazza, the TestDriver passes both the input and
formatted output data to stdout and into the resulting file.


# Source code
+ com/cscie97/ledger/
    + Account.java
    + Block.java
    + CommandProcessor.java
    + CommandProcessorException.java
    + Ledger.java
    + LedgerException.java
    + test/
        + TestDriver.java
    + Transaction.java
    
# Data files
+ errors.script                         -- script that mostly test error cases
+ errors.script.out                     -- results of running errors.script
+ ledger.script                         -- provided as part of assignment files
+ ledger.script.out                     -- results of running ledger.script
+ ledger2.script                        -- another test file for other functions
+ ledger2.script.out                    -- results of running ledger2.script

# Other files
+ Ledger Service Design Document.pdf    -- provided as part of assignment files
+ matthew_thomas_asn1_results.pdf       -- Design changes and results document
+ README.md                             -- This file
