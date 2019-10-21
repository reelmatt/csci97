package com.cscie97.store.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * {@inheritDoc}
 */
public class StoreModelService implements StoreModelServiceInterface {
    /** A list of all Customers registered with the Model Service. */
    private Map<String, Customer> customerMap;

    /** A list of all Devices registered with the Model Service. */
    private Map<String, Device> deviceMap;

    /** A list of Products known by the Model Service. */
    private Map<String, Product> productMap;

    /** A list of Stores tracked by the Model Service. */
    private Map<String, Store> storeMap;

    /** Constants for accessing location IDs -- to rethink?? */
    private static final Integer STORE = 0;
    private static final Integer AISLE = 1;
    private static final Integer SHELF = 2;
    private static final Integer DEVICE = 2;
    private static final Integer INVENTORY = 3;

    /**
     * StoreModelService Constructor
     *
     * Creates an instance of the Store Model Service to provision stores and
     * perform commands.
     */
    public StoreModelService() {
        this.customerMap = new HashMap<String, Customer>();
        this.deviceMap = new HashMap<String, Device>();
        this.productMap = new HashMap<String, Product>();
        this.storeMap = new HashMap<String, Store>();
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

        Basket basket = getBasket(authToken, customerId);
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
    public void clearBasket(String authToken,
                            String customerId) throws StoreModelServiceException {
        // Throws exception if Customer does not exist
        Customer customer = getCustomer(authToken, customerId);
        customer.clearBasket();
        return;
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

        // Throws Exception if Store does not exist
        Store store = getStore(authToken, storeId);

        // Check if Aisle already exists in store
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
    public Basket defineBasket(String authToken,
                               String customerId) throws StoreModelServiceException {
        // Throws Exception if Customer does not exist
        Customer customer = getCustomer(authToken, customerId);

        // Create new Basket
        Basket newBasket = new Basket(customerId);

        // Associate it with their account
        customer.setBasket(newBasket);
        return newBasket;
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
        if (this.customerMap.get(customerId) != null) {
            throw new StoreModelServiceException(
                    "define customer",
                    "A customer with id " + customerId + " already exists."
            );
        }

        // Create customer
        Customer newCustomer = new Customer(customerId, firstName, lastName, type, email, account);

        // Add to Store list
        this.customerMap.put(customerId, newCustomer);
        return newCustomer;
    }

    /**
     * {@inheritDoc}
     */
    public Device defineDevice(String authToken,
                               String deviceId,
                               String name,
                               String type,
                               String fullyQualifiedAisleId) throws StoreModelServiceException {
        // All Device information must be present
        if (deviceId == null || name == null || type == null || fullyQualifiedAisleId == null) {
            throw new StoreModelServiceException(
                "define device", "Required device information is missing."
            );
        }

        // Needs to be globablly unique
        if (this.deviceMap.containsKey(deviceId)) {
            throw new StoreModelServiceException(
                "define device", "A device already exists with id " + deviceId
            );
        }

        // Validate the location id references a real Aisle
        try {
            Aisle aisle = getAisle(authToken, fullyQualifiedAisleId);
        } catch (StoreModelServiceException e) {
            throw new StoreModelServiceException(
                "define device", "An aisle could not be found at " + fullyQualifiedAisleId
            );
        }

        // Create new Sensor/Appliance
        Enum deviceType;
        Device newDevice = null;

        // Check if type is an Appliance
        if ( (deviceType = ApplianceType.getType(type)) != null) {
            newDevice = new Appliance(deviceId, name, fullyQualifiedAisleId, (ApplianceType) deviceType);
        } else if ((deviceType = SensorType.getType(type)) != null) {
            // Check if type is a Sensor
            newDevice = new Sensor(deviceId, name, fullyQualifiedAisleId, (SensorType) deviceType);
        } else {
            // Type does not match any known Appliance or Sensor
            throw new StoreModelServiceException("define device", "Type " + type + " is not a known device type.");
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

        // Throws an Exception if the Shelf does not exist
        Shelf shelf = getShelf(authToken, location);

        // Check that the Shelf doesn't already contain 'inventoryId'
        if (shelf.getInventory(inventoryId) != null) {
            throw new StoreModelServiceException(
                "define inventory",
                String.format("Inventory %s already exists in aisle %s", inventoryId, shelf.getId())
            );
        }

        // Throws an Exception of the Product does not exist
        Product product = getProduct(authToken, productId);

        // Check that the Inventory's product temperature matches the shelf temperature.
        if (product.getTemperature() != shelf.getTemperature()) {
            throw new StoreModelServiceException(
                "define inventory",
                String.format("The product (%s) and shelf (%s) temperatures do not match.",
                              product.getTemperature(), shelf.getTemperature())
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

        // Store the individual parts
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
        Aisle aisle = null;

        try {
            aisle = getStore(authToken, storeId).getAisle(aisleId);
        } catch (StoreModelServiceException e) {
            throw new StoreModelServiceException(
                // getStore() will raise Exception, getAisle() is checked below
                "define shelf", "The Store " + storeId + " does not exist."
            );
        }

        // Check if the Aisle exists
        if (aisle == null) {
            throw new StoreModelServiceException(
                "define shelf", "The Aisle " + aisleId + " does not exist."
            );
        } else if (aisle.getShelf(shelfId) != null) {
            throw new StoreModelServiceException(
                "define shelf",
                String.format("Shelf '%s' already exists in aisle '%s'", shelfId, aisleId)
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
    public Aisle getAisle(String authToken,
                          String locationId) throws StoreModelServiceException {
        // Must include a location
        if (locationId == null) {
            throw new StoreModelServiceException("get aisle", "No location provided.");
        }

        // Parse the fully qualified location string
        String[] ids = parseLocationIdentifier(locationId);

        // Validate all location ids are present
        if (ids.length < 2) {
            throw new StoreModelServiceException("get aisle", "Missing location arguments.");
        }

        // Store the individual parts
        String storeId, aisleId;
        storeId = ids[STORE];
        aisleId = ids[AISLE];

        // Lookup the aisle
        Aisle aisle = null;
        try {
            aisle = getStore(authToken, storeId).getAisle(aisleId);
        } catch (StoreModelServiceException e) {
            // getStore() will raise Exception, getAisle() is checked below
            throw new StoreModelServiceException(
                "get aisle",
                String.format("The Store %s does not exist.", storeId)
            );
        }

        // Check that an Aisle was found
        if (aisle == null) {
            throw new StoreModelServiceException(
                "get aisle",
                String.format("An aisle with id '%s' does not exist in store '%s'.", aisleId, storeId)
            );
        }

        return aisle;
    }

    /**
     * {@inheritDoc}
     */
    public Basket getBasket(String authToken,
                            String customerId) throws StoreModelServiceException {
        Customer customer = getCustomer(authToken, customerId);
        Basket basket = customer.getBasket();

        if (basket == null) {
            throw new StoreModelServiceException(
                    "get customer_basket",
                    String.format("Customer '%s' does not have a basket.", customerId)
            );
        }

        return basket;
    }

    /**
     * {@inheritDoc}
     */
    public Customer getCustomer(String authToken,
                                String customerId) throws StoreModelServiceException {
        Customer customer = this.customerMap.get(customerId);

        if (customer == null) {
            throw new StoreModelServiceException(
                    "get customer",
                    String.format("A Customer with id '%s' is not registered with the StoreModelService.", customerId)
            );
        }

        return customer;
    }

    /**
     * {@inheritDoc}
     */
    public Device getDevice(String authToken,
                            String deviceId) throws StoreModelServiceException {
        Device device = this.deviceMap.get(deviceId);

        if (device == null) {
            throw new StoreModelServiceException(
                "get device",
                String.format("A device with id '%s' does not exist.", deviceId)
            );
        }

        return device;
    }

    /**
     * {@inheritDoc}
     */
    public Inventory getInventory(String authToken,
                                  String locationId) throws StoreModelServiceException {
        // Must include a location
        if (locationId == null) {
            throw new StoreModelServiceException("get inventory", "No location provided.");
        }

        // Parse the fully qualified location string
        String[] ids = parseLocationIdentifier(locationId);

        // Validate all location ids are present
        if (ids.length < 4) {
            throw new StoreModelServiceException("get inventory", "Missing location arguments.");
        }

        // Store the individual parts
        String storeId, aisleId, shelfId, inventoryId;
        storeId = ids[STORE];
        aisleId = ids[AISLE];
        shelfId = ids[SHELF];
        inventoryId = ids[INVENTORY];

        // Lookup the inventory
        Inventory inventory = null;
        try {
            inventory = getStore(authToken, storeId).getAisle(aisleId).getShelf(shelfId).getInventory(inventoryId);
        } catch (NullPointerException e) {
            // getStore() will raise Exception, getInventory() is checked below
            throw new StoreModelServiceException(
                "get inventory",
                String.format("The Aisle %s or Shelf %s does not exist.", aisleId, shelfId)
            );
        }

        // Check that an Inventory was found
        if (inventory == null) {
            throw new StoreModelServiceException(
                    "get inventory",
                    String.format("An inventory with id '%s' does not exist in location '%s:%s:%s'.", inventoryId, storeId, aisleId, shelfId)
            );
        }

        return inventory;
    }

    /**
     * {@inheritDoc}
     */
    public Product getProduct(String authToken,
                              String productId) throws StoreModelServiceException  {
        Product product = this.productMap.get(productId);

        if (product == null) {
            throw new StoreModelServiceException(
                "get product",
                String.format("A product with id '%s' does not exist.", productId)
            );
        }

        return product;
    }

    /**
     * {@inheritDoc}
     */
    public Shelf getShelf(String authToken,
                          String locationId) throws StoreModelServiceException {
        // Must include a location
        if (locationId == null) {
            throw new StoreModelServiceException("get aisle", "No location provided.");
        }

        // Parse the fully qualified location string
        String[] ids = parseLocationIdentifier(locationId);

        // Validate all location ids are present
        if (ids.length < 3) {
            throw new StoreModelServiceException("get shelf", "Missing location arguments.");
        }

        // Store the individual parts
        String storeId, aisleId, shelfId;
        storeId = ids[STORE];
        aisleId = ids[AISLE];
        shelfId = ids[SHELF];

        // Lookup the shelf
        Shelf shelf = null;
        try {
            shelf = getStore(authToken, storeId).getAisle(aisleId).getShelf(shelfId);
        } catch (NullPointerException e) {
            // getStore() will raise Exception, getShelf() is checked below
            throw new StoreModelServiceException("get shelf", "The Aisle " + aisleId + " does not exist.");
        }

        // Check that a Shelf was found
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
    public Store getStore(String authToken,
                          String storeId) throws StoreModelServiceException {
        Store store = this.storeMap.get(storeId);

        if (store == null) {
            throw new StoreModelServiceException("get store", "A store does not exist with id " + storeId);
        }

        return store;
    }

    /**
     * {@inheritDoc}
     */
    public void receiveCommand(String authToken,
                               String deviceId,
                               String message) throws StoreModelServiceException {
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
        appliance.processCommand(message);
        return;
    }

    /**
     * {@inheritDoc}
     */
    public void receiveEvent(String authToken,
                             String deviceId,
                             String event) throws StoreModelServiceException {
        // Event string must be present
        if (event == null) {
            throw new StoreModelServiceException("create event", "Event is required.");
        }

        // Print event
        System.out.println("Device '" + deviceId + "' emitted event: " + event);
        return;
    }

    /**
     * {@inheritDoc}
     */
    public ProductAssociation removeItemFromBasket(String authToken,
                                                   String customerId,
                                                   String productId,
                                                   Integer itemCount) throws StoreModelServiceException {
        // All information must be present
        if (customerId == null || productId == null) {
            throw new StoreModelServiceException("remove basket_item", "Required information is missing.");
        }

        // Item count must be >= 0 (to decrement the count)
        if (itemCount < 0) {
            throw new StoreModelServiceException("remove basket_item", "Count to remove from item must be >= 0.");
        }

        Basket basket = getBasket(authToken, customerId);
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

    /**
     * {@inheritDoc}
     */
    public void updateCustomer(String authToken,
                                 String customerId,
                                 String location) throws StoreModelServiceException {
        // Must include a location
        if (location == null) {
            throw new StoreModelServiceException("update customer", "No location provided.");
        }

        // Throws Exception if doesn't exist
        Customer customer = getCustomer(authToken, customerId);

        // Parse location identification
        String[] ids = parseLocationIdentifier(location);

        // Check location identification is valid
        if (ids.length < 2) {
            throw new StoreModelServiceException("update customer", "Missing location arguments");
        }

        // Throws Exception if Objects don't exist
        Store store = getStore(authToken, ids[STORE]);
        Aisle aisle = getAisle(authToken, location);

        // Get current time
        Date now = new Date();

        // Update customer's location
        customer.setLocation(store, aisle, now.toString());

        // Print results
        String status = String.format("Customer %s's location updated. %s.",
                                      customer.getId(), customer.customerLocation());
        System.out.println(status);
        return;
    }

    /**
     * {@inheritDoc}
     */
    public void updateInventory(String authToken,
                                String fullyQualifiedInventoryId,
                                Integer amount) throws StoreModelServiceException {
        // Must include a location
        if (fullyQualifiedInventoryId == null) {
            throw new StoreModelServiceException("update inventory", "No location provided.");
        }

        // Find the Inventory, throws Exception if doesn't exist
        Inventory inventory = getInventory(authToken, fullyQualifiedInventoryId);

        // Placeholder counts
        Integer currentCount = inventory.getCount();
        Integer newCount = currentCount + amount;

        // Check new count is valid
        if (newCount < 0) {
            throw new StoreModelServiceException("update inventory", "Count change would be less than 0.");
        } else if (newCount > inventory.getCapacity()) {
            throw new StoreModelServiceException("update inventory", "Count change would exceed capacity.");
        }

        // Perform update
        inventory.updateCount(amount);

        // Print results
        String status = String.format("Inventory '%s' updated count of '%s' from %d to %d.",
                                      inventory.getId(), inventory.getProductId(), currentCount, inventory.getCount());
        System.out.println(status);
        return;
    }

    /**
     * Construct a list of Customers currently located in the Store specified by
     * 'storeId'.
     *
     * @param   authToken   Authentication token to validate with the service.
     * @param   storeId     The store whose Customers to search for.
     * @return              List of Customer currently located in the Store.
     */
    public List<Customer> getStoreCustomers(String authToken, String storeId) {
        List<Customer> customersInStore = new ArrayList<Customer>();
        for (Customer customer : this.customerMap.values()) {
            if (storeId.equals(customer.getStore())) {
                customersInStore.add(customer);
            }
        }

        return customersInStore;
    }

    /**
     * Construct a list of Devices currently located in the Store specified by
     * 'storeId'.
     *
     * @param   authToken   Authentication token to validate with the service.
     * @param   storeId     The store whose Devices to search for.
     * @return              List of Devices currently located in the Store.
     */
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


    private String[] parseLocationIdentifier(String location) {
        return location.split(":");
    }
}
