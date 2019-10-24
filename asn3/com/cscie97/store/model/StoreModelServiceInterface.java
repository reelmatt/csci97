package com.cscie97.store.model;

import java.util.List;

/**
 * Interface for a Store Model Service.
 *
 * The interface defines basic functionality which includes creating, updating,
 * and accessing the state of store entities, including Store, Aisle, Shelf,
 * Inventory, Product, Device, Customer, and Basket objects.
 *
 * When implemented, a singleton is instantiated to provide access to the
 * Store Model Service through a public API. All methods accept an authToken
 * parameter, the functionality of which will be defined in Assignment 3.
 * For now, it is treated as an opaque string and performs no function.
 *
 * @author Matthew Thomas
 */
public interface StoreModelServiceInterface {
    /**
     * Add a given count of a Product to a Customer's Basket.
     *
     * Checks that all required parameters are included and the amount specified
     * is a postive Integer. It then retrieves the Customer's Basket and checks to
     * see if the Product already has items saved. If the Basket contains the Product,
     * the count is incremented. Otherwise, a new ProductAssociation is created with
     * the Product and 'itemCount' included.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   customerId                  The customer id.
     * @param   productId                   The product id.
     * @param   itemCount                   The amount of the Product to add.
     * @return                              The ProductAssociation specifying the Product and the
     *                                      amount stored in the Basket.
     * @throws  StoreModelServiceException  If parameters are missing or the 'itemCount' is < 0.
     *                                      The Exception is also thrown if getBasket() fails to
     *                                      retrieve a basket.
     */
    public ProductAssociation addItemToBasket(String authToken,
                                              String customerId,
                                              String productId,
                                              Integer itemCount) throws StoreModelServiceException;

    /**
     * Clear a Basket of all ProductAssociations and remove from the Customer.
     *
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   customerId                  The customer id.
     * @throws  StoreModelServiceException  If the Customer does not exist.
     */
    public void clearBasket(String authToken,
                            String customerId) throws StoreModelServiceException;

    /**
     * Create a new Aisle.
     *
     * Checks that all required parameters are included. Also checks that the requested Store
     * exists, and that an Aisle with the requested ID does not already exist within the Store.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   fullyQualifiedAisleId       Fully qualified location identifier to place the Aisle.
     *                                      Takes the form <store>:<aisle>.
     * @param   name                        The Aisle name.
     * @param   description                 Description of the aisle.
     * @param   location                    Location of the Aisle (floor | stock_room).
     * @return                              The Aisle created by the Store Model Service.
     * @throws StoreModelServiceException   If parameters are missing or the fully qualified id is missing
     *                                      tokens. An Exception is also thrown if the requested Store
     *                                      does not exist, or if an Aisle with the requested ID already
     *                                      exists in that Store.
     */
    public Aisle defineAisle(String authToken,
                             String fullyQualifiedAisleId,
                             String name,
                             String description,
                             Location location) throws StoreModelServiceException;

    /**
     * Create a Basket associated with a Customer.
     *
     * Retrieves the Customer specified by 'customerId' and creates a new Basket to
     * associate with their account.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   customerId                  The customer id.
     * @return                              The Basket created by the Store Model Service.
     * @throws  StoreModelServiceException  If the Customer does not exist.
     */
    public Basket defineBasket(String authToken,
                               String customerId) throws StoreModelServiceException;

    /**
     * Create a new Customer.
     *
     * Checks that all required parameters are included and that a customer with the provided
     * id does not already exist within the Store Model Service.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   customerId                  The customer id.
     * @param   firstName                   Customer's first name.
     * @param   lastName                    Customer's last name.
     * @param   type                        Type of customer (registered | guest).
     * @param   email                       Customer's email address.
     * @param   account                     Customer's Ledger account address.
     * @return                              The Customer created by the Store Model Service.
     * @throws  StoreModelServiceException  If parameters are missing or the Customer already
     *                                      exists within the Store Model Service.
     */
    public Customer defineCustomer(String authToken,
                                   String customerId,
                                   String firstName,
                                   String lastName,
                                   CustomerType type,
                                   String email,
                                   String account) throws StoreModelServiceException;

    /**
     * Create a new Device.
     *
     * Checks that all required parameters are included, that the Device does not already
     * exist, and the requested location exists in the Store Model Service. The new Device
     * must also match either an ApplianceType or SensorType to be created.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   deviceId                    The device id.
     * @param   name                        Name of the device.
     * @param   type                        The type of Device. For Sensors: (microphone | camera).
     *                                      For Appliances: (speaker | robot | turnstile).
     * @param   fullyQualifiedAisleId       Fully-qualified location to use for Shelf lookup.
     *                                      Of the form <store>:<aisle>:<shelf>
     * @return                              The Device created by the Store Model Service.
     * @throws  StoreModelServiceException  If parameters are missing or the Device already exists
     *                                      in the Store Model Service. An Exception is also thrown
     *                                      if the Store or Aisle requested do not exist. The Device
     *                                      type must also match a valid SensorType or ApplianceType.
     */
    public Device defineDevice(String authToken,
                               String deviceId,
                               String name,
                               String type,
                               String fullyQualifiedAisleId) throws StoreModelServiceException;


    /**
     * Create a new Inventory.
     *
     * Checks that all required parameters are included and that the count >= 0 and the
     * count <= capacity. The method then checks that the shelf requested by 'location' and
     * the product requested by 'productId' exist and that an Inventory with 'inventoryId'
     * does not exist in the Shelf. The final check performed is that the Product and Shelf
     * temperatures match. If all tests pass, the Inventory is created and added to the Shelf.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   inventoryId                 The inventory id.
     * @param   location                    Fully-qualified location id to use for placement.
     *                                      Of the form <store>:<aisle>:<shelf>
     * @param   capacity                    Total capacity of the Inventory.
     * @param   count                       The current amount of the product in Inventory.
     * @param   productId                   The Product tracked by the Inventory.
     * @return                              The Inventory created by the Store Model Service.
     * @throws StoreModelServiceException   If...
     */
    public Inventory defineInventory(String authToken,
                                String inventoryId,
                                String location,
                                Integer capacity,
                                Integer count,
                                String productId) throws StoreModelServiceException;

    /**
     * Create a new Product.
     *
     * Checks that all required parameters are included and that the price is >= 0.
     * The productId is globally unique (not already in the productMap).
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   productId                   The product id.
     * @param   name                        Name of the product.
     * @param   description                 Description of the product.
     * @param   size                        Product size (weight and/or volume).
     * @param   category                    Product category.
     * @param   price                       Individual unit price (must be >= 0).
     * @param   temperature                 Temperature (frozen | refrigerated | ambient | warm | hot).
     * @return                              The Product created by the Store Model Service.
     * @throws StoreModelServiceException   If parameters are missing or if the price is < 0. An
     *                                      Exception is also thrown if the Product already exists
     *                                      in the Store Model Service.
     */
    public Product defineProduct(String authToken,
                              String productId,
                              String name,
                              String description,
                              String size,
                              String category,
                              Integer price,
                              Temperature temperature) throws StoreModelServiceException;


    /**
     * Create a new Shelf.
     *
     * Checks that all required parameters are included. Also checks that the requested
     * Store and Aisle exist, and a Shelf with the requested ID does not already exist
     * in that Store and Aisle.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   fullyQualifiedShelfId       Fully-qualified location to use for Shelf lookup.
     *                                      Of the form <store>:<aisle>:<shelf>
     * @param   name                        Name of the Shelf.
     * @param   level                       Level of the Shelf (high | medium | low).
     * @param   description                 Description of the Shelf.
     * @param   temperature                 Temperature (frozen | refrigerated | ambient | warm | hot).
     * @return                              The Shelf created by the Store Model Service.
     * @throws  StoreModelServiceException  If parameters are missing or the fully qualified id is missing
     *                                      tokens. An Exception is also thrown if the requested Store or
     *                                      Aisle do not exist, or if a Shelf with the requested ID already
     *                                      exists in that location.
     */
    public Shelf defineShelf(String authToken,
                            String fullyQualifiedShelfId,
                            String name,
                            Level level,
                            String description,
                            Temperature temperature) throws StoreModelServiceException;

    /**
     * Create a new Store.
     *
     * Checks that all required parameters are included and that the storeId is
     * globally unique (not already in the storeMap).
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   storeId                     The store id.
     * @param   name                        Name of the store.
     * @param   address                     Address of the store.
     * @return                              The Store created by the Store Model Service.
     * @throws  StoreModelServiceException  If parameters are missing, or if a Store with
     *                                      the same id already exists.
     */
    public Store defineStore(String authToken,
                             String storeId,
                             String name,
                             String address) throws StoreModelServiceException;

    /**
     * Retrieve the Inventory specified by 'locationId'.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   locationId                  Fully-qualified location to use for Inventory lookup.
     *                                      Of the form <store>:<aisle>:<shelf>:<inventory>
     * @return                              Inventory on success. Otherwise, raise Exception.
     * @throws  StoreModelServiceException  If the locationId is incomplete, the parent Store,
     *                                      Aisle, or Shelf do not exist, or if the Inventory
     *                                      is not found.
     */
    public Aisle getAisle(String authToken,
                          String locationId) throws StoreModelServiceException;

    /**
     * Retrieve the Basket associated with the Customer specified by 'customerId'.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   customerId                  The Customer to look for.
     * @return                              Basket on success. Otherwise, raise Exception.
     * @throws  StoreModelServiceException  If the Customer with the given id does not exist, or
     *                                      they do not have a Basket.
     */
    public Basket getBasket(String authToken,
                            String customerId) throws StoreModelServiceException;

    /**
     * Retrieve the Customer specified by 'customerId'.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   customerId                  The Customer to look for.
     * @return                              Customer on success. Otherwise, raise Exception.
     * @throws  StoreModelServiceException  If the Customer does not exist with the given id.
     */
    public Customer getCustomer(String authToken,
                                String customerId) throws StoreModelServiceException;

    /**
     * Retrieve the Device specified by 'deviceId'.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   deviceId                    The Device to look for.
     * @return                              Device on success. Otherwise, raise Exception.
     * @throws  StoreModelServiceException  If the Device does not exist with the given id.
     */
    public Device getDevice(String authToken,
                            String deviceId) throws StoreModelServiceException;

    /**
     * Retrieve the Inventory specified by 'locationId'.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   locationId                  Fully-qualified location to use for Inventory lookup.
     *                                      Of the form <store>:<aisle>:<shelf>:<inventory>
     * @return                              Inventory on success. Otherwise, raise Exception.
     * @throws  StoreModelServiceException  If the locationId is incomplete, the parent Store,
     *                                      Aisle, or Shelf do not exist, or if the Inventory
     *                                      is not found.
     */
    public Inventory getInventory(String authToken,
                                  String locationId) throws StoreModelServiceException;

    /**
     * Retrieve the Product specified by 'productId'.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   productId                   The Product to look for.
     * @return                              Product on success. Otherwise, raise Exception.
     * @throws  StoreModelServiceException  If the Product does not exist with the given id.
     */
    public Product getProduct(String authToken,
                              String productId) throws StoreModelServiceException;

    /**
     * Retrieve the Shelf specified by 'locationId'.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   locationId                  Fully-qualified location to use for Shelf lookup.
     *                                      Of the form <store>:<aisle>:<shelf>
     * @return                              Shelf on success. Otherwise, raise Exception.
     * @throws  StoreModelServiceException  If the locationId is incomplete, the parent Store or
     *                                      Aisle does not exist, or if the Shelf is not found.
     */
    public Shelf getShelf(String authToken,
                          String locationId) throws StoreModelServiceException;

    /**
     * Retrieve the Store specified by 'storeId'.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   storeId                     The Store to look for.
     * @return                              Store on success. Otherwise, raise Exception.
     * @throws  StoreModelServiceException  If the Store does not exist with the given id.
     */
    public Store getStore(String authToken,
                          String storeId) throws StoreModelServiceException;


    public List<Customer> getStoreCustomers(String authToken, String storeId);
    public List<Device> getStoreDevices(String authToken,
                                        String storeId) throws StoreModelServiceException;

    /**
     * Receive a command to forward to an Appliance.
     *
     * When a command is received, it is forwarded to the requested Appliance.
     * Only Appliance devices can be controlled, so an error will occur if a
     * command is sent to a Sensor. Commands will be implemented in Assignment 3.
     * For this assignment, the method checks the command exists and belongs to
     * an Appliance. If it does, it forwards the command to the Appliance which
     * prints the command to stdout.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   deviceId                    The Device that will process the command.
     * @param   message                     The command message.
     * @throws  StoreModelServiceException  If the command message is not provided, the Device
     *                                      does not exist, or the Device is not an Appliance
     *                                      (only Appliances can receive commands).
     */
    public void receiveCommand(String authToken,
                               String deviceId,
                               String message) throws StoreModelServiceException;

    /**
     * Receive an event from a Device.
     *
     * When a physical Device emits and event, the Store Model Service is notified
     * via receiveEvent(). Device events will be implemented in Assignment 3. For
     * this assignment, the method checks that an event was received and, if so,
     * prints the event to stdout.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   deviceId                    The device which created the event (simulated).
     * @param   event                       The event that was created.
     * @throws  StoreModelServiceException  If the 'event' is not provided.
     */
    public void receiveEvent(String authToken,
                             String deviceId,
                             String event) throws StoreModelServiceException;

    /**
     * Remove a given amount of a product from a Customer's basket.
     *
     * Retrieve the basket and the ProductAssociation tracking the product
     * specified by productId. If the removal of 'itemCount' would result in
     * a count of 0 or less for the given product, the Product is removed
     * entirely from the Basket. Otherwise, 'itemCount' is subtracted from the
     * total count.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   customerId                  The customer who owns the basket.
     * @param   productId                   The product to reduce the count of.
     * @param   itemCount                   The amount of the product to remove.
     * @return                              An updated ProductAssociation reflecting the current
     *                                      count of the Product in the Basket. If all of a
     *                                      product is removed, null is returned.
     * @throws  StoreModelServiceException  If parameters are missing or if the count is negative.
     *                                      The Exception is also thrown if calls to getBasket()
     *                                      fail, or if the Product is not in the Basket. An
     *                                      Exception is also thrown if the Product is found, but
     *                                      cannot be removed from the Basket.
     */
    public ProductAssociation removeItemFromBasket(String authToken,
                                                   String customerId,
                                                   String productId,
                                                   Integer itemCount) throws StoreModelServiceException;

    /**
     * Update a customer's location.
     *
     * Retrieve the customer requested by customerId. If the customer exists,
     * parse the location and retrieve the corresponding Store and Aisle objects.
     * Generate the current time and pass all parameters to the customer to update
     * their location. If successfuly, details of the change are printed to stdout.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   customerId                  The customer's location to update.
     * @param   location                    Absolute location of customer (store:aisle).
     * @throws  StoreModelServiceException  If an incomplete location is provided. The Exception
     *                                      is also thrown if calls to getCustomer(), getStore(),
     *                                      or getAisle() fail.
     */
    public void updateCustomer(String authToken,
                               String customerId,
                               String location) throws StoreModelServiceException;

    /**
     * Update an inventory's product count.
     *
     * Retrieve the inventory specified by the fully qualified id. If an
     * inventory exists, the amount change is tested to ensure the new count
     * will remain >= 0 and <= capacity. If true, the inventory count is updated
     * and the change details are printed to stdout.
     *
     * @param   authToken                   Authentication token to validate with the service.
     * @param   fullyQualifiedInventoryId   Absolute location of the inventory (store:aisle:shelf:inventory)
     * @param   amount                      The amount the change the 'count' by.
     * @throws  StoreModelServiceException  If the new inventory count would be < 0, or the count
     *                                      would exceed capacity. The Exception is also thrown if
     *                                      the call to getInventory() fails.
     */
    public void updateInventory(String authToken,
                                String fullyQualifiedInventoryId,
                                Integer updateCount) throws StoreModelServiceException;
}