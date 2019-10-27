package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.ProductAssociation;
import com.cscie97.store.model.Shelf;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Inventory;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.StoreModelServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 * BasketEventCommand.
 *
 * @author Matthew Thomas
 */
public class BasketEventCommand extends AbstractCommand {

    private Customer customer;

    private Product product;

    private Shelf shelf;

    private String action;

    private String location;

    private static final String MESSAGE = "Added %d %s to the basket. Current count is %d.";

    public BasketEventCommand(String authToken,
                              StoreModelServiceInterface storeModel,
                              Device source,
                              Customer customer,
                              Product product,
                              Shelf shelf,
                              String action,
                              String location) {
        super(authToken, storeModel, source);
        this.customer = customer;
        this.product = product;
        this.shelf = shelf;
        this.action = action;
        this.location = location;
    }

    public void execute() throws StoreControllerServiceException {
        StoreModelServiceInterface storeModel = super.getStoreModel();
        String authToken = super.getAuthToken();

        try {
            List<Inventory> inventories = this.shelf.getInventoryList();
            Inventory productInventory = null;

            // Check that Shelf contains an Inventory with specified Product
            for(Inventory inventory : inventories) {
                if (inventory.getProductId().equals(this.product.getId())) {
                    productInventory = inventory;
                    break;
                }
            }

            // Inventory does not exist
            if (productInventory == null) {
                throw new StoreControllerServiceException(
                    "basket event",
                    "The shelf " + this.shelf.getId() + " does not contain product " + this.product.getId()
                );
            }

            Integer amountAdded;
            ProductAssociation basketItem;
            String result = "";
            // Add or remove from basket
            if (action.toLowerCase().equals("adds")) {
                amountAdded = 1;

                basketItem = storeModel.addItemToBasket(authToken,
                    this.customer.getId(),
                    this.product.getId(),
                    amountAdded
                );

                result += "Added " + amountAdded;
                // Print current count of item in basket
//                System.out.print("Added 1 " + this.product.getName() + " to the basket. ");

            } else {
                amountAdded = -1;
                basketItem = storeModel.removeItemFromBasket(authToken,
                    this.customer.getId(),
                    this.product.getId(),
                    amountAdded
                );

                result += "Removed " + (amountAdded * -1);
                // Print current count of item in basket
//                System.out.print("Removed 1" + this.product.getName() + " from the basket. ");

            }

            result += " of " + this.product.getName() + " from the basket. ";
            result += "Current count is " + basketItem.getCount() + ".";

            System.out.println(result);

            // Mirror change in inventory
            String inventoryLocation = location + ":" + productInventory.getId();
            storeModel.updateInventory(authToken, inventoryLocation, (amountAdded * -1));


            // Restock command
            if (productInventory.getCount() < (productInventory.getCapacity() / 2)) {

                Integer toRestock = productInventory.getCapacity() - productInventory.getCount();

                Appliance robot = super.getOneAppliance(ApplianceType.ROBOT);
                super.sendCommand(robot.getId(), "Restock " + toRestock + " of " + this.product.getName() + " on shelf " + this.shelf.getId());
                super.getStoreModel().updateInventory(super.getAuthToken(), location + ":" + productInventory.getId(), toRestock);

                System.out.println(
                    String.format(
                        "Inventory restocked. Now has %d on shelf out of capacity of %d.",
                        productInventory.getCount(), productInventory.getCapacity()
                    )
                );
            }


        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}