package com.cscie97.store.controller;

import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Inventory;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.ProductAssociation;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.authentication.AuthToken;
import com.cscie97.store.authentication.AuthenticationException;
import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.InvalidAuthTokenException;

/**
 * FetchProductCommand.
 *
 * A FetchProductCommand is created when a Customer asks a Microphone for some
 * amount of a Product. When created, the first available Robot is tasked to go
 * to the Aisle where the Product is located, remove some number of the Product,
 * and bring it to the Customer in the Aisle where it was asked for.
 *
 * @author Matthew Thomas
 */
public class FetchProductCommand extends AbstractCommand {
    /** The Customer who made the Product request. */
    private Customer customer;

    /** The amount of Product the Customer asked for. */
    private Integer amount;

    /** The Product the Customer requested. */
    private Product product;

    /** The fully qualified Inventory location. */
    private String location;

    /**
     * FetchProductCommand Constructor.
     *
     * @param authToken     Token to authenticate with StoreModel API.
     * @param storeModel    Store Model Service to get/update state.
     * @param source        The Device which detected the event.
     * @param customer      The Customer that requested the Product.
     * @param amount        The amount of the Product requested.
     * @param product       The Product requested by the Customer.
     * @param location      The fully qualified Inventory location.
     */
    public FetchProductCommand(AuthToken authToken,
                               StoreModelServiceInterface storeModel,
                               Device source,
                               Customer customer,
                               Integer amount,
                               Product product,
                               String location) throws StoreControllerServiceException {
        super(authToken, storeModel, source);
        this.customer = customer;

        if (amount <= 0) {
            throw new StoreControllerServiceException("fetch product", "amount must be > 0");
        }
        this.amount = amount;
        this.product = product;
        this.location = location;
    }

    /**
     * {@inheritDoc}
     *
     * Ask the Model Service for one Robot and send it a command message
     * to bring the Customer the amount of product. Reduce the inventory by
     * that amount and increase the Customer basket by that amount.
     */
    public void execute() {
        AuthToken authToken = super.getAuthToken();

        try {
            Appliance robot = super.getOneAppliance(ApplianceType.ROBOT);
            String message = String.format(
                "Fetch %d of %s from %s and bring to customer.",
                this.amount, this.location, this.product.getName()
            );
            super.sendCommand(robot, message);

            Inventory inventory = super.getStoreModel().getInventory(authToken, this.location);

            // Reflect change in inventory
            super.getStoreModel().updateInventory(authToken, location, (amount * -1));

            // Store the basket item
            ProductAssociation basketItem = super.getStoreModel().addItemToBasket(
                authToken, this.customer.getId(), this.product.getId(), amount
            );

            // Print result and current count
            System.out.println(String.format(
                "Added %d of %s to the basket. Current count is %d.",
                this.amount, this.product.getName(), basketItem.getCount()
            ));

            // If Inventory is at less than half capacity, generate restock command
            if (inventory.getCount() < (inventory.getCapacity() / 2)) {
                String event = String.format("product %s inventory %s restock", this.product.getId(), this.location);
                super.getStoreModel().receiveEvent(super.getAuthToken(), super.getSource().getId(), event);
            }

        } catch (StoreModelServiceException e) {
            System.err.println(e);
        } catch (AccessDeniedException e) {
            System.err.println(e);
        } catch (AuthenticationException e) {
            System.err.println(e);
        } catch (InvalidAuthTokenException e) {
            System.err.println(e);
        }

        return;
    };
}