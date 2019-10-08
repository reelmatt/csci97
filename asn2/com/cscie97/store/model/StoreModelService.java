package com.cscie97.store.model;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * {@inheritDoc}
 *
 *
 */
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

    /**
     * StoreModelService Constructor
     *
     *
     */
    public StoreModelService() {
        this.storeMap = new HashMap<String, Store>();
        this.productMap = new HashMap<String, Product>();
        this.deviceMap = new HashMap<String, Device>();
        this.masterCustomerList = new HashMap<String, Customer>();
    }

    /**
     * {@inheritDoc}
     */
    public ProductAssociation addItemToBasket(String authToken,
                                              String customerId,
                                              String productId,
                                              Integer itemCount) throws StoreModelServiceException {
        // All information must be present
        if (customerId == null || productId == null) {
            throw new StoreModelServiceException("add basket_item", "Required information is missing.");
        }

        // Item count must be >= 0
        if (itemCount < 0) {
            throw new StoreModelServiceException("add basket_item", "Item count must be >= 0.");
        }

        Basket basket = getCustomerBasket(authToken, customerId);
        ProductAssociation basketItem = basket.getBasketItem(productId);

        // Check if the Basket already contains some of the item
        if (basketItem != null) {
            // Update that count
            basketItem.updateCount(itemCount);
        } else {
            // Otherwise, add new item to basket with given count
            Product product = getProduct(authToken, productId);
            basketItem = new ProductAssociation(itemCount, product);
            basket.addItem(basketItem);

        }

        return basketItem;
    }

    /**
     * {@inheritDoc}
     */
    public void clearBasket(String authToken, String customerId) throws StoreModelServiceException {
        Customer customer = getCustomer(authToken, customerId);
        customer.clearBasket();

        return;
    }

    /**
     * {@inheritDoc}
     */
    public void commandAppliance(String authToken, String deviceId, String message)
            throws StoreModelServiceException {
        // Command message must be present
        if (message == null) {
            throw new StoreModelServiceException("create command", "Message is required to create a command.");
        }

        // Get the Device specified
        Device device = getDevice(authToken, deviceId);

        // Make sure it is an Appliance, not a Sensor
        if (! (device instanceof Appliance)) {
            throw new StoreModelServiceException("create command", device.getName() + " cannot process commands.");
        }

        // Cast the device, and send it the message
        Appliance appliance = (Appliance) device;
        appliance.respondToCommand(message);
        return;
    }

    /**
     * {@inheritDoc}
     */
    public Basket createCustomerBasket(String authToken, String customerId) throws StoreModelServiceException {
        Customer customer = getCustomer(authToken, customerId);

        Basket newBasket = new Basket(customerId);
        customer.setBasket(newBasket);
        return newBasket;
    }

    /**
     * {@inheritDoc}
     */
    public Aisle defineAisle(String authToken,
                             String fullyQualifiedAisleId,
                             String name,
                             String description,
                             Location location) throws StoreModelServiceException {

        // All Aisle information must be present
        if (fullyQualifiedAisleId == null || name == null || description == null || location == null) {
            throw new StoreModelServiceException("define aisle", "Required aisle information is missing.");
        }

        // Parse location identification
        String[] ids = parseLocationIdentifier(fullyQualifiedAisleId);
        String storeId, aisleId;

        // Check location identification is valid
        try {
            storeId = ids[STORE];
            aisleId = ids[AISLE];
        } catch (IndexOutOfBoundsException e) {
            throw new StoreModelServiceException("define aisle", "Missing location ID(s).");
        }

        // Check if Aisle already exists in store
        Store store = getStore(authToken, storeId);
        if (store.getAisle(aisleId) != null) {
            throw new StoreModelServiceException(
                    "define aisle",
                    String.format("Aisle '%s' already exists in store '%s'", aisleId, storeId)
            );
        }

        // Doesn't already exist, so create it
        Aisle newAisle = new Aisle(aisleId, name, description, location);
        store.addAisle(newAisle);
        return newAisle;
    }

    /**
     * {@inheritDoc}
     */
    public Customer defineCustomer(String authToken,
                                   String customerId,
                                   String firstName,
                                   String lastName,
                                   CustomerType type,
                                   String email,
                                   String account) throws StoreModelServiceException {
        // All Customer information must be present
        if (customerId == null || type == null || email == null || account == null) {
            throw new StoreModelServiceException(
                "define customer",
                "Required Customer information is missing."
            );
        }

        if (firstName == null || lastName == null) {
            throw new StoreModelServiceException(
                "define customer",
                "Customer name is incomplete."
            );
        }

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
        return newCustomer;
    }

    /**
     * {@inheritDoc}
     * Additional details about the method implementation goes here...
     */
    public Device defineDevice(String authToken,
                               String deviceId,
                               String name,
                               String type,
                               String fullyQualifiedAisleLocation) throws StoreModelServiceException {
        // All Device information must be present
        if (deviceId == null || name == null || type == null || fullyQualifiedAisleLocation == null) {
            throw new StoreModelServiceException(
                "define device",
                    "Required device information is missing."
            );
        }

        // Needs to be globablly unique
        if (this.deviceMap.containsKey(deviceId)) {
            throw new StoreModelServiceException(
                "define device",
                "A device already exists with id " + deviceId
            );
        }

        // Create new Sensor/Appliance
        Device newDevice = null;
        Enum deviceType;
        if ( (deviceType = ApplianceType.getType(type)) != null) {
            newDevice = new Appliance(deviceId, name, fullyQualifiedAisleLocation, (ApplianceType) deviceType);
        } else if ((deviceType = SensorType.getType(type)) != null) {
            newDevice = new Sensor(deviceId, name, fullyQualifiedAisleLocation, (SensorType) deviceType);
        }

        // Add Device to the StoreModelService list
        this.deviceMap.put(deviceId, newDevice);
        return newDevice;
    }

    /**
     * {@inheritDoc}
     */
    public Inventory defineInventory(String authToken,
                                     String inventoryId,
                                     String location,
                                     Integer capacity,
                                     Integer count,
                                     String productId) throws StoreModelServiceException {
        // All Inventory information must be present
        if (inventoryId == null || location == null || productId == null) {
            throw new StoreModelServiceException(
                "define inventory",
                "Required inventory information is missing."
            );
        }

        // Validate count and capacity values
        if (count < 0 || count > capacity) {
            throw new StoreModelServiceException(
                "define inventory",
                "Inventory count is invalid. Must be >= 0 and <= capcaity."
            );
        }

        // Check that the Shelf doesn't already contain 'inventoryId'
        Shelf shelf = getShelf(authToken, location);
        if (shelf.getInventory(inventoryId) != null) {
            throw new StoreModelServiceException(
                "define inventory",
                String.format("Inventory %s already exists in aisle %s", inventoryId, shelf.getId())
            );
        }

        // Check that the product exists
        Product product = getProduct(authToken, productId);
        if (product == null) {
            throw new StoreModelServiceException(
                "define inventory",
                String.format("The Product '%s' does not exist.", productId)
            );
        }

        // Create inventory and add to the shelf
        Inventory newInventory = new Inventory(inventoryId, capacity, count, product);
        shelf.addInventory(newInventory);
        return newInventory;
    }

    /**
     * {@inheritDoc}
     */
    public Product defineProduct(String authToken,
                                 String productId,
                                 String name,
                                 String description,
                                 String size,
                                 String category,
                                 Integer price,
                                 Temperature temperature) throws StoreModelServiceException {
        // All Product information must be present
        if (productId == null || name == null || description == null || size == null || category == null) {
            throw new StoreModelServiceException("define product", "Required product information is missing.");
        }

        // Validate price value
        if (price < 0) {
            throw new StoreModelServiceException("define product", "Product price is invalid.");
        }

        // Check if a product already exists
        if (this.productMap.containsKey(productId)) {
            throw new StoreModelServiceException("define product", "That product already exists.");
        }

        // Create new product and add to list
        Product newProduct = new Product(productId, name, description, size, category, price, temperature);
        this.productMap.put(productId, newProduct);
        return newProduct;
    }

    /**
     * {@inheritDoc}
     */
    public Shelf defineShelf(String authToken,
                             String fullyQualifiedShelfId,
                             String name,
                             Level level,
                             String description,
                             Temperature temperature) throws StoreModelServiceException {
        // All Shelf information must be present
        if (fullyQualifiedShelfId == null || name == null || level == null || description == null) {
            throw new StoreModelServiceException("define shelf", "Some shelf info is missing.");
        }

        // Parse location identification
        String[] ids = parseLocationIdentifier(fullyQualifiedShelfId);
        String storeId, aisleId, shelfId;

        // Check location identification is valid
        try {
            storeId = ids[STORE];
            aisleId = ids[AISLE];
            shelfId = ids[SHELF];
        } catch (IndexOutOfBoundsException e) {
            throw new StoreModelServiceException("define shelf", "Missing location ID(s).");
        }

        // Check if Shelf already exists in a given Aisle of a given store
        // TODO -- getAisle() could return null instead of Exception because is Store method
        Aisle aisle = getStore(authToken, storeId).getAisle(aisleId);

        if (aisle.getShelf(shelfId) != null) {
            throw new StoreModelServiceException(
                    "define shelf",
                    String.format(
                        "Shelf '%s' already exists in aisle '%s'", shelfId, aisleId
                    )
            );
        }

        // Doesn't already exist, so create it
        Shelf newShelf = new Shelf(shelfId, name, level, description, temperature);
        aisle.addShelf(newShelf);
        return newShelf;
    }

    /**
     * {@inheritDoc}
     */
    public Store defineStore(String authToken,
                            String storeId,
                            String name,
                            String address) throws StoreModelServiceException {
        // All store information must be present
        if (storeId == null || name == null || address == null) {
            throw new StoreModelServiceException("define store", "Required store information is missing.");
        }

        // Store needs to be globablly unique
        if (this.storeMap.containsKey(storeId)) {
            throw new StoreModelServiceException("define store", "A store already exists with id " + storeId);
        }

        // Create new store and add to list
        Store newStore = new Store(storeId, name, address);
        this.storeMap.put(storeId, newStore);
        return newStore;
    }



    /**
     * {@inheritDoc}
     */
    public Aisle getAisle(String authToken, String locationId) throws StoreModelServiceException {
        // Parse the fully qualified location string
        String[] ids = parseLocationIdentifier(locationId);
        String storeId, aisleId;

        // Check location identification is valid
        try {
            storeId = ids[STORE];
            aisleId = ids[AISLE];
        } catch (IndexOutOfBoundsException e) {
            throw new StoreModelServiceException("get aisle", "Missing location ID(s).");
        }

        // Lookup the aisle
        Aisle aisle = getStore(authToken, storeId).getAisle(aisleId);

        if (aisle == null) {
            throw new StoreModelServiceException(
                    "get aisle",
                    String.format("An aisle with id '%s' does not exist in store '%s'.", aisleId, storeId)
            );
        }

        return aisle;
    }

    public List<ProductAssociation> getBasketItems(String authToken,
                                                   String customerId) throws StoreModelServiceException {
        Basket basket = getCustomerBasket(authToken, customerId);

        if (basket == null) {
            throw new StoreModelServiceException("get basket_items", "Customer " + customerId + " does not have a basket.");
        }

        return basket.getBasketItems();
    }

    public Customer getCustomer(String authToken, String customerId) throws StoreModelServiceException {
        Customer customer = this.masterCustomerList.get(customerId);

        if (customer == null) {
            throw new StoreModelServiceException(
                    "get customer",
                    String.format("A Customer with id '%s' is not registered with the StoreModelService.", customerId)
            );
        }

        return customer;
    }

    public Basket getCustomerBasket(String authToken, String customerId) throws StoreModelServiceException {
        Customer customer = getCustomer(authToken, customerId);

        return customer.getBasket();
    }


    public Device getDevice(String authToken, String deviceId) throws StoreModelServiceException {
        Device device = this.deviceMap.get(deviceId);

        if (device == null) {
            throw new StoreModelServiceException(
                    "get device",
                    String.format("A device with id '%s' does not exist.", deviceId)
            );
        }

        return device;
    }

    public Inventory getInventory(String authToken, String locationId) throws StoreModelServiceException {
        // Parse the fully qualified location string
        String[] ids = parseLocationIdentifier(locationId);

        // Store the individual parts
        String storeId, aisleId, shelfId, inventoryId;
        storeId = ids[STORE];
        aisleId = ids[AISLE];
        shelfId = ids[SHELF];
        inventoryId = ids[INVENTORY];

        // Lookup the shelf
        Inventory inventory = getStore(authToken, storeId).getAisle(aisleId).getShelf(shelfId).getInventory(inventoryId);

        if (inventory == null) {
            throw new StoreModelServiceException(
                    "get inventory",
                    String.format("An inventory with id '%s' does not exist in location '%s:%s:%s'.", inventoryId, storeId, aisleId, shelfId)
            );
        }

        return inventory;
    }


    public Product getProduct(String authToken, String productId) throws StoreModelServiceException  {
        Product product = this.productMap.get(productId);

        if (product == null) {
            throw new StoreModelServiceException(
                "get product",
                String.format("A product with id '%s' does not exist.", productId)
            );
        }

        return product;
    }


    public Shelf getShelf(String authToken, String locationId) throws StoreModelServiceException {
        // Parse the fully qualified location string
        String[] ids = parseLocationIdentifier(locationId);

        // Store the individual parts
        String storeId, aisleId, shelfId;
        storeId = ids[STORE];
        aisleId = ids[AISLE];
        shelfId = ids[SHELF];

        // Lookup the shelf
        Shelf shelf = getStore(authToken, storeId).getAisle(aisleId).getShelf(shelfId);

        if (shelf == null) {
            throw new StoreModelServiceException(
                    "get shelf",
                    String.format("A shelf with id '%s' does not exist in location '%s:%s'.", shelfId, storeId, aisleId)
            );
        }

        return shelf;
    }

    /**
     * {@inheritDoc}
     */
    public Store getStore(String authToken, String storeId) throws StoreModelServiceException {
        Store store = this.storeMap.get(storeId);

        if (store == null) {
            throw new StoreModelServiceException("get store", "A store does not exist with id " + storeId);
        }

        return store;
    }

    public List<Device> getStoreDevices(String authToken, String storeId) throws StoreModelServiceException {
        Store store = getStore(authToken, storeId);

        List<Device> storeDeviceList = new ArrayList<Device>();

        for (Device device : this.deviceMap.values()) {
            String deviceStoreId = device.getStore();
            if (storeId.equals(deviceStoreId)) {
                storeDeviceList.add(device);
            }
        }

        return storeDeviceList;

    }

    /**
     * {@inheritDoc}
     */
    public void updateCustomer(String authToken, String customerId, String location) throws StoreModelServiceException {
        // throws Exception if doesn't exist
        Customer customer = getCustomer(authToken, customerId);

        // Parse location identification
        String[] ids = parseLocationIdentifier(location);

        // Check location identification is valid
        if (ids.length < 2) {
            throw new StoreModelServiceException("update customer", "Missing location arguments");
        }

        // throws Exception if Objects don't exist
        Store store = getStore(authToken, ids[STORE]);
        Aisle aisle = getAisle(authToken, location);
//        Aisle aisle = store.getAisle(ids[AISLE]);
        customer.setLocation(store, aisle);


        return;
    }

    /**
     * {@inheritDoc}
     */
    public void updateInventory(String authToken,
                                String fullyQualifiedInventoryId,
                                Integer updateCount) throws StoreModelServiceException {
        // Find the Inventory
        Inventory inventory = getInventory(authToken, fullyQualifiedInventoryId);

        // Placeholder counts
        Integer currentCount = inventory.getCount();
        Integer newCount = currentCount + updateCount;

        // Check new count is valid
        if (newCount < 0) {
            throw new StoreModelServiceException("update inventory", "Count change would be less than 0.");
        } else if (newCount > inventory.getCapacity()) {
            throw new StoreModelServiceException("update inventory", "Count change would exceed capacity.");
        }

        // Perform update
        inventory.updateCount(updateCount);

        // TODO remove output
        System.out.println("INVENTORY: updated count from " + currentCount + " to " + inventory.getCount());
        return;
    }




//    public List<Customer> getStoreCustomers(String authToken, String storeId) throws StoreModelServiceException {
//        Store store = getStore(authToken, storeId);
//
//        List<Customer> storeCustomerList = new ArrayList<Customer>();
//
//        for (Customer customer : this.masterCustomerList.values()) {
//            String customerStoreId = customer.getStore();
//
//            if (storeId.equals(customerStoreId)) {
//                storeCustomerList.add(customer);
//            }
//        }
//
//        return storeCustomerList;
//    }
    public List<Customer> getStoreCustomers(String authToken, String storeId) {
        List<Customer> customersInStore = new ArrayList<Customer>();
        for (Customer customer : this.masterCustomerList.values()) {
            if (storeId.equals(customer.getStore())) {
                customersInStore.add(customer);
            }
        }

        return customersInStore;
    }

    public ProductAssociation removeItemFromBasket(String authToken,
                                     String customerId,
                                     String productId,
                                     Integer itemCount) throws StoreModelServiceException{
//        Basket basket = getCustomerBasket(authToken, basketId);
//        Product product = getProduct(authToken, productId);
//
//        ProductAssociation pa = basket.removeItem(productId, itemCount);


        // All information must be present
        if (customerId == null || productId == null) {
            throw new StoreModelServiceException("remove basket_item", "Required information is missing.");
        }

        // Item count must be >= 0 (to decrement the count)
        if (itemCount < 0) {
            throw new StoreModelServiceException("remove basket_item", "Count to remove from item must be >= 0.");
        }

        Basket basket = getCustomerBasket(authToken, customerId);
        ProductAssociation basketItem = basket.getBasketItem(productId);

        // Check if the Basket already contains some of the item
        if (basketItem == null) {
            throw new StoreModelServiceException("remove basket_item", productId + " is not in the Customer basket.");
        }

        // Placeholder counts
        Integer newCount = basketItem.getCount() - itemCount;

        // Check new count is valid
        if (newCount <= 0) {
            if (! basket.removeItem(basketItem)) {
                throw new StoreModelServiceException("remove basket_item", "Product could not be removed from basket.");
            }

            return null;
        }

        // Update that count
        basketItem.decrementCount(itemCount);
        return basketItem;
    }







//    private List<Customer> getCustomerList() {
//        return this.masterCustomerList.values();
//    }
    private String[] parseLocationIdentifier(String location) {
        return location.split(":");
    }

    public String toString() {
        return "StoreModelService: customers = ";
    }
}
