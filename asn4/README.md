# Submission:

As per the assignment handout, my assignment 4 submission includes the source
code, data files, test results, design document, and a document describing the
results of my implementation. This README file provides some additional 
information regarding my submission.

All files are contained within the zip file “matthew_thomas_assignment4.zip”.
Note: the command for compilation differs slightly from the command provided in the
assignment handout. As noted in Piazza post @263, the Ledger Service (from assignment 1)
should be included in the compilation command. The working commands to compile and run
the assignment are:

// Compile program
javac com/cscie97/store/model/*.java com/cscie97/store/controller/*.java com/cscie97/store/authentication/*.java com/cscie97/ledger/*.java com/cscie97/store/test/*.java

// Run test script
java -cp . com.cscie97.store.test.TestDriver <test script file(s)>


where `<test script file(s)>` is the name of a test script file or files. To simplify
the test files, for assignment 4 I re-wrote my TestDriver to accept multiple test files
to run in sequence. This allows for functionality to be broken up among several files, one
for each service. Comments describing each file, and the tests, can be found within the files.
My test run outputs can be found in the root level, with ".out" appended
to the file name. As per post @67 on Piazza, the TestDriver passes both the input and
formatted output data to stdout and into the resulting file.


To simulate Permissions with the Store Controller and Model Services, and to obtain the
results for "store24.out", the following command was run:

java -cp . com.cscie97.store.test.TestDriver store.script authentication.script ledger.script controller.script

For error testing, and to obtain the results for "errors.script.out", the following
command was run:

java -cp . com.cscie97.store.test.TestDriver errors.script



# Source code
+ com/cscie97/
    + ledger/
        + Account.java
        + Block.java
        + Ledger.java
        + LedgerException.java
        + Transaction.java
    + store/
        + authentication/
            + AccessDeniedException.java
            + AuthenticationException.java
            + AuthenticationService.java
            + AuthenticationServiceInterface.java
            + AuthToken.java
            + AuthVisitor.java
            + Entitlement.java
            + EntitlementInterface.java
            + InvalidAuthTokenException.java
            + InventoryVisitor.java
            + Permission.java
            + Resource.java
            + ResourceRole.java
            + Role.java
            + User.java
            + Visitor.java
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
+ authentication.script
+ errors.script
    + script that mostly test error cases
+ errors.script.out
    + results of running errors.script
+ controller.script
    + From assignment 3 submission. Written by myself and modified for assignment
    4 to test Authentication Service features.
+ ledger.script
    + From assignment 1 submission
+ store24.out
    + Results from running commands for Ledger, Store Model, Controller, and
    Authentication Services. The input is spread across four files (one for each
    service). Commands for Authentication Service only can be found in
    authentication.script
+ store.script
    + From assignment 2 submission. Written by Adaeze Ezeh and provided in course
    files on Canvas. Modifications made by myself for assignment 3 and further
    modifications for assignment 4 to incorporate face/voice prints.


# Other files
+ Diagrams
	+ CheckAccountBalance.png			-- Sequence diagram with AuthVisitor
	+ Class Diagram.png     			-- Class diagram
	+ GetInventory.png				    -- Sequence diagram with InventoryVisitor
	+ UseCase Diagram.png				-- Use Case diagram
+ matthew_thomas_asn4_results.pdf       -- Design changes and results document
+ matthew_thomas_asn4_design.pdf        -- My design document
+ matthew_thomas_asn3_design.pdf        -- My assignment 3 design (for reference)
+ matthew_thomas_asn2_design.pdf        -- My assignment 2 design (for reference)
+ README.md                             -- This file
