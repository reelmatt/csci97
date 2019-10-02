package com.cscie97.store.model;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class StoreModelService implements StoreModelServiceInterface {

    private String masterCustomerList;
    private Map<String, Store> storeList;
    private String productList;

    public StoreModelService() {
        this.masterCustomerList = "customers";
        this.storeList = new HashMap<String, Store>();
        this.productList = "products";
    }

    public void defineStore(String id, String name, String address) throws StoreModelServiceException {
        // Needs to be globablly unique
        if (this.storeList.containsKey(id)) {
            throw new StoreModelServiceException("define store", "A store already exists with id " + id);
        }

        Store newStore = new Store(id, name, address);
        this.storeList.put(id, newStore);
    }

    public void defineAisle(String id, String name, String description, String location) throws StoreModelServiceException {
        String[] ids = parseLocationString(id);

        Store store = this.storeList.get(ids[0]);
        if ( store == null ) {
            throw new StoreModelServiceException("define aisle", "A store does not exist with id " + ids[0]);
        }

        Aisle newAisle = new Aisle(Integer.parseInt(ids[1]), name, description, location);
        System.out.println("CREATED aisle: " + newAisle);
        store.addAisle(newAisle);

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

    public Store getStore(String id) {
//        int storeIndex = this.storeList.indexOf(id);
        return this.storeList.get(id);
    }

    public Map<Integer, Aisle> getAisle(String id) throws StoreModelServiceException {
        String[] ids = parseLocationString(id);

        Store store = this.storeList.get(ids[0]);
        if ( store == null ) {
            throw new StoreModelServiceException("define aisle", "A store does not exist with id " + ids[0]);
        }

        Map<Integer, Aisle> aisles = new HashMap<Integer, Aisle>();

        try {

            aisles.put(Integer.parseInt(ids[1]), store.getAisle(ids[1]));
        } catch (ArrayIndexOutOfBoundsException e) {
            aisles = store.getAisleList();

        }
//        if(ids[1] != null) {
//        } else {
//        }

        return aisles;
    }

    public void getCustomer() {

    }

    private String[] parseLocationString(String location) {
//        List<String> locations = new ArrayList<String>();
        String[] locations = location.split(":");
        return locations;
    }

    public String toString() {
        return "StoreModelService: customers = " + this.masterCustomerList;
    }
}
