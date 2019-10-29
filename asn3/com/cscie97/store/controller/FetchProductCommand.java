package com.cscie97.store.controller;

import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.StoreModelServiceException;
import com.cscie97.store.model.StoreModelServiceInterface;

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

    /**
     * FetchProductCommand Constructor.
     * @param authToken
     * @param storeModel
     * @param source
     * @param customer
     * @param amount
     * @param product
     */
    public FetchProductCommand(String authToken, StoreModelServiceInterface storeModel, Device source, Customer customer, Integer amount, Product product) {
        super(authToken, storeModel, source);
        this.customer = customer;
        this.amount = amount;
        this.product = product;
    }

    /**
     * {@inheritDoc}
     *
     * Ask the Model Service for one Robot and send it a command message
     * to bring the Customer the amount of product.
     */
    public void execute() {
        try {
            Appliance robot = super.getOneAppliance(ApplianceType.ROBOT);
            String message = String.format("Fetch %d of %s from <aisle> and <shelf> and bring to customer.", this.amount, this.product.getName());
            super.sendCommand(robot, message);

        } catch (StoreModelServiceException e) {
            System.err.println(e);
        }

        return;
    };
}