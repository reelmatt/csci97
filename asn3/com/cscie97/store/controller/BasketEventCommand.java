package com.cscie97.store.controller;

import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.ProductAssociation;
import com.cscie97.store.model.Shelf;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Inventory;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;


/**
 * BasketEventCommand.
 *
 * A BasketEventCommand is created when a Camera detects a Customer moves a
 * product to/from their basket. This command then updates the state of the
 * StoreModelService to increment/decrement the Customer Basket and Shelf
 * Inventory. After the update is performed, a Robot is commanded to add more of
 * the Product to the Shelf. Cameras detect movement of a single object at a
 * time. Should the Customer remove two of the same Products from a Shelf, two
 * BasketEventCommands would be created and executed.
 *
 * @author Matthew Thomas
 */
public class BasketEventCommand extends AbstractCommand {
    /** The Customer that was detected. */
    private Customer customer;

    /** The Product that was added/removed. */
    private Product product;

    /** The Shelf where the Product is located. */
    private Shelf shelf;

    /** The action the Customer took ('adds' or 'removes'). */
    private String action;

    /** The fully qualified shelf location. */
    private String shelfLocation;

    /** Formatted result message. */
    private static final String MESSAGE = "Added %d %s to the basket. Current count is %d.";

    /**
     * BasketEventCommand Constructor.
     *
     * @param authToken
     * @param storeModel
     * @param source
     * @param customer
     * @param product
     * @param shelf
     * @param action
     * @param shelfLocation
     */
    public BasketEventCommand(String authToken,
                              StoreModelServiceInterface storeModel,
                              Device source,
                              Customer customer,
                              Product product,
                              Shelf shelf,
                              String action,
                              String shelfLocation) {
        super(authToken, storeModel, source);
        this.customer = customer;
        this.product = product;
        this.shelf = shelf;
        this.action = action;
        this.shelfLocation = shelfLocation;
    }

    /**
     * {@inheritDoc}
     *
     * Check that the shelf contains an inventory which tracks the specified
     * product. If one exists, check the action for 'adds' or 'removes'. If 'adds',
     * add 1 of the item to the Customer's basket, print the result, and remove 1
     * of the item from the inventory. If 'removes', do the reverse (remove from
     * basket, add to inventory). After the operation, if the inventory contains
     * less than half of its capacity, generate a RestockCommand to have a
     * Robot add more Product back to the shelf.
     *
     * @throws StoreControllerServiceException  If the Shelf does not contain an Inventory
     *                                          which tracks the Product, or the action
     *                                          is unsupported.
     */
    public void execute() throws StoreControllerServiceException {
        StoreModelServiceInterface storeModel = super.getStoreModel();
        String authToken = super.getAuthToken();

        try {
            // Get the inventory where the Product is located
            Inventory productInventory = this.shelf.getProductInventory(this.product.getId());

            // Inventory does not exist
            if (productInventory == null) {
                throw new StoreControllerServiceException(
                    "basket event",
                    String.format("The shelf %s does not contain product %s", this.shelf.getId(), this.product.getId())
                );
            }

            // Construct fully qualified Inventory location
            String inventoryLocation = shelfLocation + ":" + productInventory.getId();

            // Store the basket item from adding/removing
            ProductAssociation basketItem;

            // Add or remove from basket
            if (this.action.equals("adds")) {
                // Add item to basket
                basketItem = storeModel.addItemToBasket(authToken, this.customer.getId(), this.product.getId(), 1);

                // Print result and current count
                System.out.println(String.format(
                    "Added 1 of %s to the basket. Current count is %d.",
                    this.product.getName(), basketItem.getCount()
                ));

                // Reflect change in inventory
                storeModel.updateInventory(authToken, inventoryLocation, -1);
            } else if (this.action.equals("removes")){
                // Remove item from basket
                basketItem = storeModel.removeItemFromBasket(authToken, this.customer.getId(), this.product.getId(), -1);

                // Print result and current count
                System.out.println(String.format(
                    "Removed 1 of %s from the basket. Current count is %d.",
                    this.product.getName(), basketItem.getCount()
                ));

                // Reflect change in inventory
                storeModel.updateInventory(authToken, inventoryLocation, 1);
            } else {
                throw new StoreControllerServiceException(
                        "basket event", "The action '" + this.action + "' is unsupported. Only 'adds' and 'removes' are allowed."
                );
            }

            // If Inventory is at less than half capacity, generate restock command
            if (productInventory.getCount() < (productInventory.getCapacity() / 2)) {
                String event = String.format("product %s inventory %s restock", this.product.getId(), inventoryLocation);
                super.getStoreModel().receiveEvent(super.getAuthToken(), super.getSource().getId(), event);
            }
        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}