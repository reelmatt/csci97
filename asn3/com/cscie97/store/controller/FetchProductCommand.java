package com.cscie97.store.controller;

import com.cscie97.store.model.Device;
import com.cscie97.store.model.Appliance;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Product;
import com.cscie97.store.model.StoreModelServiceInterface;
import com.cscie97.store.model.ApplianceType;
import com.cscie97.store.model.StoreModelServiceException;
import java.util.List;
import java.util.ArrayList;

/**
 * FetchProductCommand.
 *
 * @author Matthew Thomas
 */
public class FetchProductCommand extends AbstractCommand {
    private Customer customer;

    private Integer amount;

    private Product product;

    public FetchProductCommand(String authToken, StoreModelServiceInterface storeModel, Device source, Customer customer, Integer amount, Product product) {
        super(authToken, storeModel, source);
        this.customer = customer;
        this.amount = amount;
        this.product = product;
    }

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