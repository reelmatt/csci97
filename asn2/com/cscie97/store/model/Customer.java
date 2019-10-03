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
    private Aisle location;

    public Customer (String id, String first, String last, String type, String email, String account) {
        this.id = id;
        this.firstName = first;
        this.lastName = last;
        this.type = CustomerType.GUEST;
        this.email = email;
        this.accountAddress = account;
        this.ageGroup = Age.ADULT;
        this.location = null;

    }

    public void getBasket() {

    }

    public void clearBasket() {

    }

    public void addItem() {

    }

    public void removeItem() {

    }

    public void setLocation(Aisle aisle) {
        this.location = aisle;
    }

    public String customerName() {
        return this.firstName + " " + this.lastName;
    }

    public String toString() {
        return "Customer #" + this.id + " -- " + this.customerName();
    }
}