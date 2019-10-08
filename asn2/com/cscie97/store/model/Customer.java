package com.cscie97.store.model;

/**
 *
 *
 * @author Matthew Thomas
 */
public class Customer {
    /** */
    private enum Age {CHILD, ADULT};

    /** */
    private String id;

    /** */
    private String lastName;

    /** */
    private String firstName;

    /** */
    private CustomerType type;

    /** */
    private String email;

    /** */
    private String accountAddress;

    /** */
    private String lastSeen;

    /** */
    private Age ageGroup;

    /** */
    private Store store;

    /** */
    private Aisle aisle;

    /** */
    private Basket basket;

    /**
     * Customer Constructor.
     *
     *
     *
     * @param id
     * @param first
     * @param last
     * @param type
     * @param email
     * @param account
     */
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

    /** */
    public Basket getBasket() {
        return this.basket;
    }

    /** */
    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    /** */
    public void clearBasket() {
        this.basket.clear();
        this.basket = null;
    }

    /** */
    public void addItem() {

    }

    /** */
    public void removeItem() {

    }

    /** */
    public void setLocation(Store store, Aisle aisle) {
        this.store = store;
        this.aisle = aisle;
    }

    /** */
    public String getId() {
        return this.id;
    }

    /** */
    public String getStore() {
        if (this.store == null) {
            return null;
        }

//        String[] ids = this.location.split(":");
//        return ids[0];
        return this.store.getId();
    }

    /** */
    public String customerName() {
        return this.firstName + " " + this.lastName;
    }

    /** */
    public String customerLocation() {
        String location = "Currently located @ ";
        location += (this.store == null) ? "N/a" : this.store.getName();
        location += (this.aisle == null) ? "" : this.aisle.getId();
        return location;
//        return ": currently @ " + this.store.getName() + ", aisle" + this.aisle.getId();
    }

    /**
     * Override default toString method.
     *
     * Displays details of the customer, including their id, name, type, email,
     * and account address.
     */
    public String toString() {
        String customer;

        customer = String.format("Customer '%s': %s\n", this.id, customerName());
        customer += "\t" + customerLocation() + "\n";
        customer += String.format("\tType: %s\n", this.type);
        customer += String.format("\tEmail: %s\n", this.email);
        customer += String.format("\tAccount address: %s", this.accountAddress);

        return customer;
    }
}