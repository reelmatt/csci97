package com.cscie97.store.model;

public interface StoreModelServiceInterface {

    public void defineStore(String id, String name, String address) throws StoreModelServiceException;

    public void defineAisle(String id, String name, String description, String location) throws StoreModelServiceException;

    public void defineShelf();

    public void defineInventory();

    public void defineCustomer();

    public void defineDevice();

    public void defineProduct();

    public void getCustomer();
}