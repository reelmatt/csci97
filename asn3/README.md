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
        +
    + store/
        + controller/
            + 
            + 
            + 
        + model/
            + Aisle.java
            + Appliance.java
            + ApplianceType.java
            + Basket.java
            + CommandProcessor.java
            + CommandProcessorException.java
            + Customer.java
            + CustomerType.java
            + Device.java
            + Inventory.java
            + Level.java
            + Location.java
            + Product.java
            + ProductAssociation.java
            + Sensor.java
            + SensorType.java
            + Shelf.java
            + Store.java
            + StoreModelService.java
            + StoreModelServiceException.java
            + StoreModelServiceInteface.java
            + Temperature.java
        + test/
            + TestDriver.java
    
# Data files
+ errors.script
    + script that mostly test error cases
+ errors.script.out
    + results of running errors.script
+ store.script
    + Written by Adaeze Ezeh and provided as part of assignment files. Modified slightly
    by myself to accomodate command syntax changes.
+ store.script.out
    + results of running store.script

# Other files
+ matthew_thomas_asn3_design.pdf        -- My design document
+ matthew_thomas_asn3_results.pdf       -- Design changes and results document
+ README.md                             -- This file
