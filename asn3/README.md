# Submission:

As per the assignment handout, my assignment 3 submission includes the source
code, data files, test results, design document, and a document describing the results
of my implementation. This README file provides some additional information regarding
my submission.

All files are contained within the zip file “matthew_thomas_assignment3.zip”. The commands
to compile and run the assignment are:

    javac com/cscie97/store/model/*.java com/cscie97/store/controller/*.java com/cscie97/ledger/*.java com/cscie97/store/test/*.java
    java -cp . com.cscie97.store.test.TestDriver <test script file>


where `<test file name>` is the name of the test script file. My test run
outputs can also be found in the root level, with “.out” appended to the test
file’s name. As per post @67 on Piazza, the TestDriver passes both the input and
formatted output data to stdout and into the resulting file.

Note: I made a few minor modifications to the command syntax. The updated syntax can be
seen in comment lines in the scripts themselves, as well as in the results PDF as part of
this submission.

# Source code
+ com/cscie97/
    + ledger/
        + Account.java
        + Block.java
        + Ledger.java
        + LedgerException.java
        + Transaction.java
    + store/
        + controller/
            + AbstractCommand.java
            + AssistCustomerCommand.java
            + BasketEventCommand.java
            + BrokenGlassCommand.java
            + CheckAccountBalanceCommand.java
            + CheckoutCommand.java
            + CleaningCommand.java
            + Command.java
            + CommandFactory.java
            + CommandProcessor.java
            + CommandProcessorException.java
            + CustomerSeenCommand.java
            + EmergencyCommand.java
            + EnterStoreCommand.java
            + FetchProductCommand.java
            + MissingPersonCommand.java
            + RestockCommand.java
            + StoreControllerService.java
        	+ StoreControllerServiceException.java
            + StoreControllerServiceInterface.java
        + model/
            + Aisle.java
            + Appliance.java
            + ApplianceType.java
            + Basket.java
            + Customer.java
            + CustomerType.java
            + Device.java
            + Inventory.java
            + Level.java
            + Location.java
            + Observer.java (new in Assignment 3)
            + Product.java
            + ProductAssociation.java
            + Sensor.java
            + SensorType.java
            + Shelf.java
            + Store.java
            + StoreModelService.java (modified in assignment 3)
            + StoreModelServiceException.java
            + StoreModelServiceInteface.java (modified in assignment 3)
            + Subject.java (new in Assignment 3)
            + Temperature.java
        + test/
            + TestDriver.java
    
# Data files
+ errors.script
    + script that mostly test error cases
+ errors.script.out
    + results of running errors.script
+ controller.script
    + Modified from the store.script file written by Adaeze Ezeh and provided as part 
    of assignment 2 files. Modified by myself to accomodate command syntax changes (for
    Store Model Service). All Controller-specific tests were written by me and noted in
    the test file.
+ controller.script.out
    + results of running controller.script

# Other files
+ matthew_thomas_asn3_design.pdf        -- My design document
+ matthew_thomas_asn3_results.pdf       -- Design changes and results document
+ README.md                             -- This file
