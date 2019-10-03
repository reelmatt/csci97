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
    private Map<String, Product> productList;

    public StoreModelService() {
        this.masterCustomerList = "customers";
        this.storeList = new HashMap<String, Store>();
        this.productList = new HashMap<String, Product>();
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
        Store store = getStore(id);

        String[] ids = parseLocationString(id);
        Aisle newAisle = new Aisle(Integer.parseInt(ids[1]), name, description, location);
        System.out.println("CREATED aisle: " + newAisle);

        store.addAisle(newAisle);
    }

    public void defineShelf(String id, String name, String level, String description, String temperature) throws StoreModelServiceException {
        String[] ids = parseLocationString(id);

        Aisle aisle = getAisle(ids[0], ids[1]);

        Shelf newShelf = new Shelf(ids[2], name, level, description, temperature);
        System.out.println("CREATED shelf: " + newShelf);
        aisle.addShelf(newShelf);

    }

    public void defineInventory(String id, String location, Integer capacity, Integer count, String productId) throws StoreModelServiceException {
        String[] ids = parseLocationString(location);

        Shelf shelf = getShelf(id);
        Product product = getProduct(productId);

        Inventory inventory = new Inventory(id, capacity, count, product);

        shelf.addInventory(inventory);
    }

    public void defineCustomer() {

    }

    public void defineDevice() {

    }

    public void defineProduct(String id, String name, String description, Integer size, String category, Integer price, String temperature) throws StoreModelServiceException {
        Product newProduct = new Product(id, name, description, size, category, price, temperature);

        this.productList.put(id, newProduct);
    }

    private Product getProduct(String productId) {
        return this.productList.get(productId);
    }

    private Store getStore(String id) throws StoreModelServiceException {
        String[] ids = parseLocationString(id);

        Store store = null;

        try {
            store = this.storeList.get(ids[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new StoreModelServiceException("get store", "Missing store ID.");
        }

        if (store == null) {
            throw new StoreModelServiceException("get store", "A store does not exist with id " + ids[0]);
        }

        return store;
    }

    public void showStore(String id) throws StoreModelServiceException {
//        int storeIndex = this.storeList.indexOf(id);
        System.out.println(getStore(id));
//        System.out.println(this.storeList.get(id));
        return;
//        return this.storeList.get(id);
    }

    public Aisle getAisle(String storeId, String aisleId) throws StoreModelServiceException {
//        String[] ids = parseLocationString(id);

        Store store = this.storeList.get(storeId);
        if ( store == null ) {
            throw new StoreModelServiceException("get aisle", "A store does not exist with id " + storeId);
        }

        return store.getAisle(aisleId);
    }

    public void showAisle(String id) throws StoreModelServiceException {
        String[] ids = parseLocationString(id);


        if (ids.length == 1) {
            Store store = this.storeList.get(ids[0]);
            if ( store == null ) {
                throw new StoreModelServiceException("define aisle", "A store does not exist with id " + ids[0]);
            }

            Map<Integer, Aisle> aisles = store.getAisleList();

            // Iterate through accounts to retrieve their current balances.
            for (Map.Entry<Integer, Aisle> entry : aisles.entrySet()) {
                System.out.println("aisle == " + entry.getValue());
            }
        } else if (ids.length == 2) {
            Aisle aisle = getAisle(ids[0], ids[1]);
            System.out.println("aisle == " + aisle);
        }


        return;
    }

    private Shelf getShelf(String id) throws StoreModelServiceException {
        String[] ids = parseLocationString(id);
        Aisle aisle = getAisle(ids[0], ids[1]);

        Shelf shelf = aisle.getShelf(ids[2]);
        System.out.println("SERVICE: shelf == " + shelf);
        return shelf;
    }

    public void showShelf(String id) throws StoreModelServiceException {
        System.out.println("showShelf() is " + getShelf(id));
        return;
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
