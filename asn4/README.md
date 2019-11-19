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
java -cp . com.cscie97.store.test.TestDriver <test script file>


where `<test script file>` is the name of the test script file. My test run
outputs can also be found in the root level, with “.out” appended to the test
file’s name. As per post @67 on Piazza, the TestDriver passes both the input and
formatted output data to stdout and into the resulting file.

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
            + 
            + 
            + 
            + 
            + 
            + 
            + 
            + 
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
+ authentication.script
    + Modified from the store.script and controller.script files used in submissions
    for assignments 2 and 3. Many Store Model commands were written by Adaeze Ezeh
    and provided on Canvas. All Controller-specific and Authentication-specific tests
    were written by me and noted in the test file. Some syntax was changed from
    assignment handouts to fit my CommandProcessor.
+ authentication.script.out
    + results of running authentication.script

# Other files
+ Diagrams
	+ Architecture.png					-- Component diagram from class handouts
	+ CheckAccountBalance.png			-- Sequence diagram
	+ Class Diagram-Full.png			-- Full class diagram, with Controller
										   and Model packages
	+ Class Diagram-StoreController.png	-- Subset of Class diagram, showing only
										   Controller classes
	+ EmergencyCommand.png				-- Sequence diagram
	+ UseCase Diagram.png				-- Use Case diagram
+ matthew_thomas_asn4_design.pdf        -- My design document
+ matthew_thomas_asn3_design.pdf        -- My assignment 3 design (for reference)
+ matthew_thomas_asn2_design.pdf        -- My assignment 2 design (for reference)
+ matthew_thomas_asn4_results.pdf       -- Design changes and results document
+ README.md                             -- This file
