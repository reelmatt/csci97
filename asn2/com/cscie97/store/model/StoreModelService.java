package com.cscie97.store.model;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class StoreModelService implements StoreModelServiceInterface {

    private Map<String, Store> storeMap;
    private Map<String, Product> productMap;
    private Map<String, Customer> masterCustomerList;

    public StoreModelService() {
        this.storeMap = new HashMap<String, Store>();
        this.productMap = new HashMap<String, Product>();
        this.masterCustomerList = new HashMap<String, Customer>();
    }

    /**
     * {@inheritDoc}
     * Additional details about the method implmenetation goes here...
     */
    public void defineStore(String id, String name, String address) throws StoreModelServiceException {
        // Needs to be globablly unique
        if (this.storeMap.containsKey(id)) {
            throw new StoreModelServiceException("define store", "A store already exists with id " + id);
        }

        // Create new store and add to list
        Store newStore = new Store(id, name, address);
        this.storeMap.put(id, newStore);
        return;
    }

    /**
     * {@inheritDoc}
     * Additional details about the method implmenetation goes here...
     */
    private Store getStore(String id) throws StoreModelServiceException {
        Store store = this.storeMap.get(id);

        if (store == null) {
            throw new StoreModelServiceException("get store", "A store does not exist with id " + id);
        }

        return store;
    }

    /**
     * {@inheritDoc}
     * Additional details about the method implmenetation goes here...
     */
    public void showStore(String id) throws StoreModelServiceException {
        System.out.println(getStore(id));
        return;
    }


    public void defineAisle(
            String id,
            String name,
            String description,
            String location) throws StoreModelServiceException {

        String[] ids = parseLocationString(id);
        Store store = getStore(ids[0]);

        Integer aisleNumber = Integer.parseInt(ids[1]);
        if (store.getAisle(aisleNumber) != null) {
            throw new StoreModelServiceException(
                    "define aisle",
                    String.format(
                            "Aisle #%d already exists in store %s", aisleNumber, store.getId()
                    )
            );
        }

        Aisle newAisle = new Aisle(aisleNumber, name, description, location);
        store.addAisle(newAisle);
    }


    public Aisle getAisle(String storeId, String aisleId) throws StoreModelServiceException {
        return getStore(storeId).getAisle(Integer.parseInt(aisleId));
    }

    public void showAisle(String id) throws StoreModelServiceException {
        String[] ids = parseLocationString(id);

        // try/catch for getStore SMSException?
        if (ids.length < 1) {
            throw new StoreModelServiceException("show aisle", "Missing location specifiers.");
        }

        // Command included an second (aisle) ID
        if (ids.length == 2) {
            System.out.println(getAisle(ids[0], ids[1]));
        } else {
            // No Aisle number included, so get info for entire store
            System.out.println(getStore(ids[0]).printAisles());
        }

        return;
    }

    public void defineShelf(
            String id,
            String name,
            String level,
            String description,
            String temperature) throws StoreModelServiceException {

        String[] ids = parseLocationString(id);

        Aisle aisle = getAisle(ids[0], ids[1]);

        if (aisle.getShelf(ids[2]) != null) {
            throw new StoreModelServiceException(
                    "define shelf",
                    String.format(
                        "Shelf #%s already exists in aisle %d", ids[2], aisle.getNumber()
                    )
            );
        }

        Shelf newShelf = new Shelf(ids[2], name, level, description, temperature);
        System.out.println("CREATED shelf: " + newShelf);
        aisle.addShelf(newShelf);

    }

    private Shelf getShelf(String storeId, String aisleId, String shelfId) throws StoreModelServiceException {
//        String[] ids = parseLocationString(id);
        return getAisle(storeId, aisleId).getShelf(shelfId);
    }

    public void showShelf(String id) throws StoreModelServiceException {
        String[] ids = parseLocationString(id);

        if (ids.length == 1) {
            Store store = getStore(ids[0]);
            Map<Integer, Aisle> aisles = store.getAisleList();

            // Iterate through accounts to retrieve their current balances.
            for (Map.Entry<Integer, Aisle> entry : aisles.entrySet()) {
                entry.getValue().showShelves();
            }
        } else if (ids.length == 2) {
            getAisle(ids[0], ids[1]).showShelves();
        } else if (ids.length == 3) {
            System.out.println(getShelf(ids[0], ids[1], ids[2]));
        }

        return;
    }

    public void defineInventory(
            String id,
            String location,
            Integer capacity,
            Integer count,
            String productId) throws StoreModelServiceException {

        String[] ids = parseLocationString(location);

        Shelf shelf = getShelf(ids[0], ids[1], ids[2]);
        Product product = getProduct(productId);

        Inventory inventory = new Inventory(id, capacity, count, product);

        shelf.addInventory(inventory);
    }

    private Inventory getInventory(String storeId, String aisleId, String shelfId, String inventoryId) throws StoreModelServiceException {
//        String[] ids = parseLocationString(id);
        return getAisle(storeId, aisleId).getShelf(shelfId).getInventory(inventoryId);
    }

    public void showInventory(String id) throws StoreModelServiceException {
        String[] ids = parseLocationString(id);

        System.out.println(getShelf(ids[0], ids[1], ids[2]).getInventory(ids[3]));
        return;
    }

    public void defineCustomer(
            String id,
            String firstName,
            String lastName,
            String type,
            String email,
            String account) {
        // Create customer
        Customer newCustomer = new Customer(id, firstName, lastName, type, email, account);

        // Add to Store list
        this.masterCustomerList.put(id, newCustomer);

        return;
    }

    public Customer getCustomer(String id) {
        return this.masterCustomerList.get(id);
    }

    public void defineDevice(String id, String name, String type, String location) throws StoreModelServiceException {
        Device newDevice = null;
        String[] ids = parseLocationString(location);
        Aisle aisle = getAisle(ids[0], ids[1]);

        ApplianceType aType;
        if ( (aType = ApplianceType.getType(type)) != null) {
            newDevice = new Appliance(id, name, aType, aisle);
        } else if (SensorType.isSensor(type)) {
            newDevice = new Sensor(id, name, type, aisle);
        }

        System.out.println(newDevice);
        this.storeMap.get(ids[0]).addDevice(newDevice);
    }

    public void showDevice(String id) throws StoreModelServiceException {
        String[] ids = parseLocationString(id);
        System.out.println(getStore(ids[0]).getDevice(ids[2]));
//        store.getDevice(id);
    }

    public void defineProduct(
            String id,
            String name,
            String description,
            Integer size,
            String category,
            Integer price,
            String temperature) throws StoreModelServiceException {

        // Check if a product already exists
        if (this.productMap.containsKey(id)) {
            throw new StoreModelServiceException("define product", "That product already exists.");
        }

        // Create new product and add to list
        Product newProduct = new Product(id, name, description, size, category, price, temperature);
        this.productMap.put(id, newProduct);
    }

    private Product getProduct(String productId) {
        return this.productMap.get(productId);
    }

    private String[] parseLocationString(String location) {
        return location.split(":");
    }

    public String toString() {
        return "StoreModelService: customers = " + this.masterCustomerList;
    }
}
