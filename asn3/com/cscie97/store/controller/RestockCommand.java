package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.Inventory;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.Transaction;
import com.cscie97.ledger.Account;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.StoreModelServiceException;


/**
 * AssistCustomerCommand.
 *
 * @author Matthew Thomas
 */
public class RestockCommand extends AbstractCommand {

    private Product product;

    private Inventory inventory;

    private String location;

    public RestockCommand(String authToken, StoreModelServiceInterface storeModel, Device source, Product product, Inventory inventory, String location) {
        super(authToken, storeModel, source);
        this.product = product;
        this.inventory = inventory;
        this.location = location;
    }

    public void execute() {
        try {

            Integer toRestock = this.inventory.getCapacity() - this.inventory.getCount();

            Appliance robot = super.getOneAppliance(ApplianceType.ROBOT);
            super.sendCommand(robot.getId(), "Restock " + toRestock + " of " + this.product.getName() + " on shelf ");
            super.getStoreModel().updateInventory(super.getAuthToken(), this.location, toRestock);

            System.out.println(
                    String.format(
                            "Inventory restocked. Now has %d on shelf out of capacity of %d.",
                            this.inventory.getCount(), this.inventory.getCapacity()
                    )
            );

        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }
        return;
    };
}