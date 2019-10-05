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

    /** A list of all Devices registered with the Model Service. */
    private Map<String, Device> deviceMap;

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
        this.deviceMap = new HashMap<String, Device>();
        this.masterCustomerList = new HashMap<String, Customer>();
    }

    /**
     * {@inheritDoc}
     * Additional details about the method implementation goes here...
     */
    public void defineStore(String authToken,
                            String storeId,
                            String name,
                            String address)
            throws StoreModelServiceException {

        // All store information must be present
        if (storeId == null || name == null || address == null) {
            throw new StoreModelServiceException("define store", "Some store info is missing.");
        }

        // Needs to be globablly unique
        if (this.storeMap.containsKey(storeId)) {
            throw new StoreModelServiceException("define store", "A store already exists with id " + storeId);
        }

        // Create new store and add to list
        Store newStore = new Store(storeId, name, address);
        this.storeMap.put(storeId, newStore);
        return;
    }


    /**
     * {@inheritDoc}
     * Additional details about the method implementation goes here...
     */
    public void defineAisle(String authToken,
                            String fullyQualifiedAisleId,
                            String name,
                            String description,
                            Location location)
            throws StoreModelServiceException {

        // All Aisle information must be present
        if (fullyQualifiedAisleId == null || name == null || description == null || location == null) {
            throw new StoreModelServiceException("define aisle", "Some aisle info is missing.");
        }

        // Parse location identification
        String[] ids = parseLocationIdentifier(fullyQualifiedAisleId);
        String storeId;
        Integer aisleNumber;

        // Check location identification is valid
        try {
            storeId = ids[STORE];
            aisleNumber = Integer.parseInt(ids[AISLE]);
        } catch (NumberFormatException e) {
            throw new StoreModelServiceException("define aisle", "Invalid Aisle number.");
        } catch (IndexOutOfBoundsException e) {
            throw new StoreModelServiceException("define aisle", "Missing location ID.");
        }

        // Check if Aisle already exists in store
        Store store = getStore(authToken, storeId);
        if (store.getAisle(aisleNumber) != null) {
            throw new StoreModelServiceException(
                    "define aisle",
                    String.format("Aisle #%d already exists in store %s", aisleNumber, storeId)
            );
        }

        // Doesn't already exist, so create it
        Aisle newAisle = new Aisle(aisleNumber, name, description, location);
        store.addAisle(newAisle);
    }

    public void defineShelf(String authToken,
                            String fullyQualifiedShelfId,
                            String name,
                            Level level,
                            String description,
                            Temperature temperature)
            throws StoreModelServiceException {

        // All Shelf information must be present
        if (fullyQualifiedShelfId == null || name == null || level == null || description == null) {
            throw new StoreModelServiceException("define shelf", "Some shelf info is missing.");
        }

        // Parse location identification
        String[] ids = parseLocationIdentifier(fullyQualifiedShelfId);
        String storeId, shelfId;
        Integer aisleNumber;

        // Check location identification is valid
        try {
            storeId = ids[STORE];
            aisleNumber = Integer.parseInt(ids[AISLE]);
            shelfId = ids[SHELF];
        } catch (NumberFormatException e) {
            throw new StoreModelServiceException("define shelf", "Invalid Aisle number.");
        } catch (IndexOutOfBoundsException e) {
            throw new StoreModelServiceException("define shelf", "Missing location ID.");
        }

        // Check if Shelf already exists in a given Aisle of a given store
        Aisle aisle = getStore(authToken, storeId).getAisle(aisleNumber);

        if (aisle.getShelf(shelfId) != null) {
            throw new StoreModelServiceException(
                    "define shelf",
                    String.format(
                        "Shelf #%s already exists in aisle %d", ids[SHELF], aisle.getNumber()
                    )
            );
        }

        // Doesn't already exist, so create it
        Shelf newShelf = new Shelf(shelfId, name, level, description, temperature);
        aisle.addShelf(newShelf);
        return;
    }

    public void defineInventory(String authToken,
                                String inventoryId,
                                String location,
                                Integer capacity,
                                Integer count,
                                String productId)
            throws StoreModelServiceException {

        String[] ids = parseLocationIdentifier(location);

        Shelf shelf = getShelf(authToken, location);
        Product product = getProduct(authToken, productId);

        // Shelf already contains the inventory
        if (shelf.getInventory(inventoryId) != null) {
            throw new StoreModelServiceException(
                "define inventory",
                String.format("Inventory %s already exists in aisle %s", inventoryId, shelf.getId())
            );
        }

        Inventory inventory = new Inventory(inventoryId, capacity, count, product);
        shelf.addInventory(inventory);
        return;
    }

    public void defineProduct(String authToken,
                              String productId,
                              String name,
                              String description,
                              Integer size,
                              String category,
                              Double price,
                              Temperature temperature)
            throws StoreModelServiceException {

        // Check if a product already exists
        if (this.productMap.containsKey(productId)) {
            throw new StoreModelServiceException("define product", "That product already exists.");
        }

        // Create new product and add to list
        Product newProduct = new Product(productId, name, description, size, category, price, temperature);
        this.productMap.put(productId, newProduct);
    }

    public void defineCustomer(String authToken,
                               String customerId,
                               String firstName,
                               String lastName,
                               String type,
                               String email,
                               String account)
            throws StoreModelServiceException {

        // Check if customer already exists
        if (this.masterCustomerList.get(customerId) != null) {
            throw new StoreModelServiceException(
                "define customer",
                "A customer with id " + customerId + " already exists."
            );
        }

        // Create customer
        Customer newCustomer = new Customer(customerId, firstName, lastName, type, email, account);

        // Add to Store list
        this.masterCustomerList.put(customerId, newCustomer);
        return;
    }

    public void defineDevice(String authToken,
                             String deviceId,
                             String name,
                             String type,
                             String location)
            throws StoreModelServiceException {


        // Needs to be globablly unique
        if (this.deviceMap.containsKey(deviceId)) {
            throw new StoreModelServiceException("define device", "A device already exists with id " + deviceId);
        }


        Device newDevice = null;
        String[] ids = parseLocationIdentifier(location);
        Aisle aisle = getAisle(authToken, location);

        Enum deviceType;
        if ( (deviceType = ApplianceType.getType(type)) != null) {
            newDevice = new Appliance(deviceId, name, aisle, (ApplianceType) deviceType);
        } else if ((deviceType = SensorType.getType(type)) != null) {
            newDevice = new Sensor(deviceId, name, aisle, (SensorType) deviceType);
        }


        // Create new store and add to list
        this.deviceMap.put(deviceId, newDevice);
        return;
    }


    /**
     * {@inheritDoc}
     * Additional details about the method implementation goes here...
     */
    public Store getStore(String authToken, String storeId) throws StoreModelServiceException {
        Store store = this.storeMap.get(storeId);

        if (store == null) {
            throw new StoreModelServiceException("get store", "A store does not exist with id " + storeId);
        }

        return store;
    }

    public Aisle getAisle(String authToken, String locationId) throws StoreModelServiceException {
        // Parse the fully qualified location string
        String[] ids = parseLocationIdentifier(locationId);

        // Store the individual parts
        String store = ids[STORE];
        Integer aisleNumber = Integer.parseInt(ids[AISLE]);

        // Lookup the aisle
        return getStore(authToken, store).getAisle(aisleNumber);
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
        // Parse the fully qualified location string
        String[] ids = parseLocationIdentifier(locationId);

        // Store the individual parts
        String store = ids[STORE];
        Integer aisleNumber = Integer.parseInt(ids[AISLE]);
        String shelf = ids[SHELF];

        // Lookup the shelf
        return getStore(authToken, store).getAisle(aisleNumber).getShelf(shelf);
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
        // Parse the fully qualified location string
        String[] ids = parseLocationIdentifier(locationId);

        // Store the individual parts
        String store = ids[STORE];
        Integer aisleNumber = Integer.parseInt(ids[AISLE]);
        String shelf = ids[SHELF];
        String inventory = ids[INVENTORY];


        return getStore(authToken, store).getAisle(aisleNumber).getShelf(shelf).getInventory(inventory);
//        return getAisle(authToken, locationId).getShelf(ids[SHELF]).getInventory(ids[INVENTORY]);
    }


    public Product getProduct(String authToken, String productId) {
        return this.productMap.get(productId);
    }

    public Customer getCustomer(String authToken, String customerId) {
        return this.masterCustomerList.get(customerId);
    }

    public Device getDevice(String authToken, String deviceId) throws StoreModelServiceException {
        return this.deviceMap.get(deviceId);
    }

    private String[] parseLocationIdentifier(String location) {
        return location.split(":");
    }

    public String toString() {
        return "StoreModelService: customers = " + this.masterCustomerList;
    }
}
