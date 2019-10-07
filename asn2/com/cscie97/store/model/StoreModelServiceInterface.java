package com.cscie97.store.model;

public interface StoreModelServiceInterface {

    public Store defineStore(String authToken,
                            String id,
                            String name,
                            String address) throws StoreModelServiceException;

    public Aisle defineAisle(String authToken,
                            String id,
                            String name,
                            String description,
                            Location location) throws StoreModelServiceException;

    public Shelf defineShelf(String authToken,
                            String id,
                            String name,
                            Level level,
                            String description,
                            Temperature temperature) throws StoreModelServiceException;

    public Inventory defineInventory(String authToken,
                                String id,
                                String location,
                                Integer capacity,
                                Integer count,
                                String productId) throws StoreModelServiceException;

    public Device defineDevice(String authToken,
                             String id,
                             String name,
                             String type,
                             String location) throws StoreModelServiceException;

    public Product defineProduct(String authToken,
                              String id,
                              String name,
                              String description,
                              String size,
                              String category,
                              Double price,
                              Temperature temperature) throws StoreModelServiceException;

    public Customer defineCustomer(String authToken,
                               String id,
                               String firstName,
                               String lastName,
                               CustomerType type,
                               String email,
                               String account) throws StoreModelServiceException;
}