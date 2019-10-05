package com.cscie97.store.model;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class StoreModelService implements StoreModelServiceInterface {
    /** A list of Stores tracked by the Model Service. */
    private Map<String, Store> storeMap;

    /** A list of Products known by the Model Service. */
    private Map<String, Product> productMap;

    /** A list of all Customers registered with the Model Service. */
    private Map<String, Customer> masterCustomerList;

    /** Constants for accessing location IDs -- to rethink?? */
    private static final Integer STORE = 0;
    private static final Integer AISLE = 1;
    private static final Integer SHELF = 2;
    private static final Integer DEVICE = 2;
    private static final Integer INVENTORY = 3;

    /** Constructor */
    public StoreModelService() {
        this.storeMap = new HashMap<String, Store>();
        this.productMap = new HashMap<String, Product>();
        this.masterCustomerList = new HashMap<String, Customer>();
    }

    /**
     * {@inheritDoc}
     * Additional details about the method implementation goes here...
     */
    public void defineStore(String authToken,
                            String id,
                            String name,
                            String address)
            throws StoreModelServiceException {

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
     * Additional details about the method implementation goes here...
     */
    public void defineAisle(String authToken,
                            String id,
                            String name,
                            String description,
                            String location)
            throws StoreModelServiceException {

        String[] ids = parseLocationIdentifier(id);
        Store store = getStore(authToken, ids[STORE]);
        Integer aisleNumber;

        try {
            aisleNumber = Integer.parseInt(ids[AISLE]);
        } catch (NumberFormatException e) {
            throw new StoreModelServiceException("define aisle", "Invalid Aisle number.");
        }

        // Check if Aisle already exists in store
        if (store.getAisle(aisleNumber) != null) {
            throw new StoreModelServiceException(
                    "define aisle",
                    String.format(
                        "Aisle #%d already exists in store %s", aisleNumber, store.getId()
                    )
            );
        }

        // Doesn't already exist, so create it
        Aisle newAisle = new Aisle(aisleNumber, name, description, location);
        store.addAisle(newAisle);
    }

    public void defineShelf(String authToken,
                            String id,
                            String name,
                            String level,
                            String description,
                            String temperature)
            throws StoreModelServiceException {

        String[] ids = parseLocationIdentifier(id);

        Aisle aisle = getAisle(authToken, id);

        if (aisle.getShelf(ids[SHELF]) != null) {
            throw new StoreModelServiceException(
                    "define shelf",
                    String.format(
                        "Shelf #%s already exists in aisle %d", ids[SHELF], aisle.getNumber()
                    )
            );
        }

        Shelf newShelf = new Shelf(ids[SHELF], name, level, description, temperature);
        aisle.addShelf(newShelf);
        return;
    }

    public void defineInventory(String authToken,
                                String id,
                                String location,
                                Integer capacity,
                                Integer count,
                                String productId)
            throws StoreModelServiceException {

        String[] ids = parseLocationIdentifier(location);

        Shelf shelf = getShelf(authToken, location);
        Product product = getProduct(authToken, productId);

        // Shelf already contains the inventory
        if (shelf.getInventory(ids[INVENTORY]) != null) {
            throw new StoreModelServiceException(
                "define inventory",
                String.format("Inventory %s already exists in aisle %s", ids[INVENTORY], shelf.getId())
            );
        }

        Inventory inventory = new Inventory(ids[INVENTORY], capacity, count, product);
        shelf.addInventory(inventory);
        return;
    }

    public void defineProduct(String authToken,
                              String id,
                              String name,
                              String description,
                              Integer size,
                              String category,
                              Double price,
                              String temperature)
            throws StoreModelServiceException {

        // Check if a product already exists
        if (this.productMap.containsKey(id)) {
            throw new StoreModelServiceException("define product", "That product already exists.");
        }

        // Create new product and add to list
        Product newProduct = new Product(id, name, description, size, category, price, temperature);
        this.productMap.put(id, newProduct);
    }

    public void defineCustomer(String authToken,
                               String id,
                               String firstName,
                               String lastName,
                               String type,
                               String email,
                               String account)
            throws StoreModelServiceException {

        // Check if customer already exists
        if (this.masterCustomerList.get(id) != null) {
            throw new StoreModelServiceException(
                "define customer",
                "A customer with id " + id + " already exists."
            );
        }

        // Create customer
        Customer newCustomer = new Customer(id, firstName, lastName, type, email, account);

        // Add to Store list
        this.masterCustomerList.put(id, newCustomer);
        return;
    }

    public void defineDevice(String authToken,
                             String id,
                             String name,
                             String type,
                             String location)
            throws StoreModelServiceException {

        Device newDevice = null;
        String[] ids = parseLocationIdentifier(location);
        Aisle aisle = getAisle(authToken, location);

        ApplianceType aType;
        if ( (aType = ApplianceType.getType(type)) != null) {
            newDevice = new Appliance(id, name, aType, aisle);
        } else if (SensorType.isSensor(type)) {
            newDevice = new Sensor(id, name, type, aisle);
        }

        System.out.println(newDevice);
        this.storeMap.get(ids[STORE]).addDevice(newDevice);
    }


    /**
     * {@inheritDoc}
     * Additional details about the method implementation goes here...
     */
    public Store getStore(String authToken, String id) throws StoreModelServiceException {
        Store store = this.storeMap.get(id);

        if (store == null) {
            throw new StoreModelServiceException("get store", "A store does not exist with id " + id);
        }

        return store;
    }

    public Aisle getAisle(String authToken, String locationId) throws StoreModelServiceException {
        String[] ids = parseLocationIdentifier(locationId);
        return getStore(authToken, ids[STORE]).getAisle(Integer.parseInt(ids[AISLE]));
    }

//    public void showAisle(String id) throws StoreModelServiceException {
//        String[] ids = parseLocationString(id);
//
//        // try/catch for getStore SMSException?
//        if (ids.length < 1) {
//            throw new StoreModelServiceException("show aisle", "Missing location specifiers.");
//        }
//
//        // Command included an second (aisle) ID
//        if (ids.length == 2) {
//            System.out.println(getAisle(ids[STORE], ids[AISLE]));
//        } else {
//            // No Aisle number included, so get info for entire store
//            System.out.println(getStore(ids[STORE]).printAisles());
//        }
//
//        return;
//    }


    public Shelf getShelf(String authToken, String locationId) throws StoreModelServiceException {
        String[] ids = parseLocationIdentifier(locationId);
        return getAisle(authToken, locationId).getShelf(ids[SHELF]);
    }

//    public void showShelf(String id) throws StoreModelServiceException {
//        String[] ids = parseLocationIdentifier(id);
//
//        if (ids.length == 1) {
//            Store store = getStore(ids[0]);
//            Map<Integer, Aisle> aisles = store.getAisleList();
//
//            // Iterate through accounts to retrieve their current balances.
//            for (Map.Entry<Integer, Aisle> entry : aisles.entrySet()) {
//                entry.getValue().showShelves();
//            }
//        } else if (ids.length == 2) {
//            getAisle(ids[0], ids[1]).showShelves();
//        } else if (ids.length == 3) {
//            System.out.println(getShelf(ids[0], ids[1], ids[2]));
//        }
//
//        return;
//    }



    public Inventory getInventory(String authToken, String locationId) throws StoreModelServiceException {
        String[] ids = parseLocationIdentifier(locationId);
        return getAisle(authToken, locationId).getShelf(ids[SHELF]).getInventory(ids[INVENTORY]);
    }


    public Product getProduct(String authToken, String productId) {
        return this.productMap.get(productId);
    }

    public Customer getCustomer(String authToken, String id) {
        return this.masterCustomerList.get(id);
    }

    public Device getDevice(String authToken, String id) throws StoreModelServiceException {
        String[] ids = parseLocationIdentifier(id);
        return getStore(authToken, ids[STORE]).getDevice(ids[DEVICE]);
//        System.out.println(getStore(ids[0]).getDevice(ids[2]));
////        store.getDevice(id);
    }
//    public void showDevice(String authToken, String id) throws StoreModelServiceException {
//        String[] ids = parseLocationIdentifier(id);
//        System.out.println(getStore(ids[0]).getDevice(ids[2]));
////        store.getDevice(id);
//    }



    private String[] parseLocationIdentifier(String location) {
        return location.split(":");
    }

    public String toString() {
        return "StoreModelService: customers = " + this.masterCustomerList;
    }
}
