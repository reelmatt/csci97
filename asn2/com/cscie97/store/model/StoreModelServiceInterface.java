package com.cscie97.store.model;

/**
 *
 *
 * @author Matthew Thomas
 */
public interface StoreModelServiceInterface {


    public Aisle defineAisle(String authToken,
                             String id,
                             String name,
                             String description,
                             Location location) throws StoreModelServiceException;

    public Customer defineCustomer(String authToken,
                                   String id,
                                   String firstName,
                                   String lastName,
                                   CustomerType type,
                                   String email,
                                   String account) throws StoreModelServiceException;

    public Device defineDevice(String authToken,
                               String id,
                               String name,
                               String type,
                               String location) throws StoreModelServiceException;

    public Inventory defineInventory(String authToken,
                                String id,
                                String location,
                                Integer capacity,
                                Integer count,
                                String productId) throws StoreModelServiceException;



    public Product defineProduct(String authToken,
                              String id,
                              String name,
                              String description,
                              String size,
                              String category,
                              Integer price,
                              Temperature temperature) throws StoreModelServiceException;


    public Shelf defineShelf(String authToken,
                            String id,
                            String name,
                            Level level,
                            String description,
                            Temperature temperature) throws StoreModelServiceException;


    /**
     * Define a Store object.
     *
     *
     *
     * @param authToken
     * @param id
     * @param name
     * @param address
     * @return
     * @throws StoreModelServiceException
     */
    public Store defineStore(String authToken,
                             String id,
                             String name,
                             String address) throws StoreModelServiceException;

}