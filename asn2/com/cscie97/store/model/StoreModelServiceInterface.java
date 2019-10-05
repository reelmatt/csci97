package com.cscie97.store.model;

public interface StoreModelServiceInterface {

    public void defineStore(String authToken,
                            String id,
                            String name,
                            String address) throws StoreModelServiceException;

    public void defineAisle(String authToken,
                            String id,
                            String name,
                            String description,
                            Location location) throws StoreModelServiceException;

    public void defineShelf(String authToken,
                            String id,
                            String name,
                            Level level,
                            String description,
                            Temperature temperature) throws StoreModelServiceException;

    public void defineInventory(String authToken,
                                String id,
                                String location,
                                Integer capacity,
                                Integer count,
                                String productId) throws StoreModelServiceException;

    public void defineDevice(String authToken,
                             String id,
                             String name,
                             String type,
                             String location) throws StoreModelServiceException;

    public void defineProduct(String authToken,
                              String id,
                              String name,
                              String description,
                              Integer size,
                              String category,
                              Double price,
                              Temperature temperature) throws StoreModelServiceException;

    public void defineCustomer(String authToken,
                               String id,
                               String firstName,
                               String lastName,
                               String type,
                               String email,
                               String account) throws StoreModelServiceException;
}