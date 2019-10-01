package com.cscie97.store.model;

import java.util.Set;
import java.util.HashSet;

public class StoreModelService implements StoreModelServiceInterface {

    private String masterCustomerList;
    private HashSet<Store> storeList;
    private String productList;

    public StoreModelService() {
        this.masterCustomerList = "customers";
//        this.storeList = "stores";
        this.storeList = new HashSet<Store>();
        this.productList = "products";
    }

    public void defineStore(Store store) {
        System.out.println("MODELSERVICE: Defining a store: " + store);

        if (this.storeList.contains(store)) {
            System.out.println("\nThe list already contains that store.");
        } else {
            this.storeList.add(store);
            System.out.println("NOW tracking " + this.storeList.size() + " stores.");
        }

    }

    public void defineAisle() {

    }

    public void defineShelf() {

    }

    public void defineInventory() {

    }

    public void defineCustomer() {

    }

    public void defineDevice() {

    }

    public void defineProduct() {

    }

    public void getCustomer() {

    }

    public String toString() {
        return "StoreModelService: customers = " + this.masterCustomerList;
    }
}
