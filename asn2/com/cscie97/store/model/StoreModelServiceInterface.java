package com.cscie97.store.model;

public interface StoreModelServiceInterface {

    public void defineStore(String id, String name, String address) throws StoreModelServiceException;

    public void defineAisle(String id, String name, String description, String location) throws StoreModelServiceException;

    public void defineShelf(String id, String name, String level, String description, String temperature) throws StoreModelServiceException;

    public void defineInventory(String id, String location, Integer capacity, Integer count, String productId) throws StoreModelServiceException;

    public void defineCustomer();

    public void defineDevice();

    public void defineProduct(String id, String name, String description, Integer size, String category, Integer price, String temperature) throws StoreModelServiceException;

    public void getCustomer();
}