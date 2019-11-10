package com.cscie97.store.controller;

import com.cscie97.ledger.Account;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.ledger.Transaction;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Inventory;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;


/**
 * RestockCommand.
 *
 * A RestockCommand is created after a BasketEventCommand if the Inventory where
 * a Product was removed contains less than half of its capacity. If the
 * Inventory is at less than half of its capacity when a Product is removed,
 * the BasketEventCommand will trigger a Store Model Event that the Inventory
 * needs to be restocked, back to its full capacity.
 *
 * @author Matthew Thomas
 */
public class RestockCommand extends AbstractCommand {
    /** The Product to restock. */
    private Product product;

    /** The Inventory that needs more product. */
    private Inventory inventory;

    /** The fully qualified Inventory location. */
    private String location;

    /**
     * RestockCommand Constructor.
     *
     * @param   authToken   Token to authenticate with StoreModel API
     * @param   storeModel  StoreModel to get/update state.
     * @param   source      The Device which detected the event.
     * @param   product     The Product to be restocked.
     * @param   inventory   The Inventory where Product needs to be restocked.
     * @param   location    The fully qualified inventory location.
     */
    public RestockCommand(String authToken,
                          StoreModelServiceInterface storeModel,
                          Device source,
                          Product product,
                          Inventory inventory,
                          String location) {
        super(authToken, storeModel, source);
        this.product = product;
        this.inventory = inventory;
        this.location = location;
    }

    /**
     * {@inheritDoc}
     *
     * Calculate the number of product needed to bring the Inventory back up to
     * full capacity. Send a command to an available Robot to fetch the product
     * and then update the Inventory with the new product.
     */
    public void execute() {
        try {
            // Capacity will not change, save it
            Integer capacity = this.inventory.getCapacity();

            // Determine how much product is need to reach full capacity
            Integer toRestock = capacity - this.inventory.getCount();

            if (toRestock <= 0) {
                System.out.println("Inventory already at maximum capacity, " + capacity + ".");
                return;
            }

            // Send Robot to get Product for Inventory
            Appliance robot = super.getOneAppliance(ApplianceType.ROBOT);
            String message = String.format(
                "Restock %d of %s in inventory %s",
                toRestock, this.product.getName(), this.inventory.getId()
            );
            super.sendCommand(robot.getId(), message);

            // Put Product into shelf Inventory
            super.getStoreModel().updateInventory(super.getAuthToken(), this.location, toRestock);

            // Log inventory status
            System.out.println(String.format(
                "Inventory restocked. Now has %d on shelf out of capacity of %d.",
                this.inventory.getCount(), capacity
            ));

        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }
        return;
    };
}