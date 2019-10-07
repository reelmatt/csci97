package com.cscie97.store.model;

public class Customer {
    private enum Age {CHILD, ADULT};

    private String id;
    private String lastName;
    private String firstName;
    private CustomerType type;
    private String email;
    private String accountAddress;
    private String lastSeen;
    private Age ageGroup;
    private Store store;
    private Aisle aisle;

    private Basket basket;

    public Customer (String id, String first, String last, CustomerType type, String email, String account) {
        this.id = id;
        this.firstName = first;
        this.lastName = last;
        this.type = (type == null) ? CustomerType.GUEST : type;
        this.email = email;
        this.accountAddress = account;
        this.ageGroup = Age.ADULT;
        this.store = null;
        this.aisle = null;
        this.basket = null;

    }

    public Basket getBasket() {
        return this.basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }
    public void clearBasket() {
        this.basket.clear();
        this.basket = null;
    }

    public void addItem() {

    }

    public void removeItem() {

    }

    public void setLocation(Store store, Aisle aisle) {
        this.store = store;
        this.aisle = aisle;
    }

    public String getId() {
        return this.id;
    }
    public String getStore() {
        if (this.store == null) {
            return null;
        }

//        String[] ids = this.location.split(":");
//        return ids[0];
        return this.store.getId();
    }

    public String customerName() {
        return this.firstName + " " + this.lastName;
    }

    public String customerLocation() {
        String location = "Currently @ ";
        location += (this.store == null) ? "N/a" : this.store.getName();
        location += (this.aisle == null) ? "" : this.aisle.getId();
        return location;
//        return ": currently @ " + this.store.getName() + ", aisle" + this.aisle.getId();
    }
    public String toString() {
        return "Customer #" + this.id + " -- " + this.customerName() + ": " + this.customerLocation();
    }
}