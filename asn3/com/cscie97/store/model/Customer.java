package com.cscie97.store.model;

/**
 * A Customer known by the Store 24X7 System.
 *
 * Each Customer has a unique identifier within the Store 24X7 System that is
 * assigned by the Store Model Service. It is the Customer's responsibility to
 * register for an account with the Ledger Service and to report that account
 * address to the Store Model Service.
 *
 * Other information about the Customer is maintained by the Store Model Service
 * in response to Device events as the Customer moves around a Store located
 * within the Store 24X7 System.
 *
 * @author Matthew Thomas
 */
public class Customer {
    /** Age group options for Customers. */
    private enum Age {CHILD, ADULT};

    /** Customer ID. */
    private String id;

    /** Customer's first name. */
    private String firstName;

    /** Customer's last name. */
    private String lastName;

    /** Type of Customer (registered or guest). */
    private CustomerType type;

    /** Customer's email address. */
    private String email;

    /** Customer's Ledger account address. */
    private String accountAddress;

    /** Time the Customer's location was last updated. */
    private String lastSeen;

    /** Customer's Age (child, adult). */
    private Age ageGroup;

    /** Store where Customer is located. */
    private Store store;

    /** Aisle within Store where Customer is located. */
    private Aisle aisle;

    /** Customer's Basket. */
    private Basket basket;

    /**
     * Customer Constructor.
     *
     * Creates a Customer with a unique id and various contact information including
     * name, email, customer type, and Ledger account address. Other information,
     * including location, time last seen, and a shopping Basket are initialized
     * to null and updated in response to Device events when the Customer enters,
     * leaves, or moves around the Store.
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
        this.lastSeen = null;
        this.ageGroup = Age.ADULT;
        this.store = null;
        this.aisle = null;
        this.basket = null;
    }

    /**
     * Removes all Products in the Basket and dissociates the Basket
     * from the Customer.
     */
    public void clearBasket() {
        if (this.basket == null) {
            return;
        }

        this.basket.clear();
        this.basket = null;
    }

    /** Returns the Basket associated with the Customer. */
    public Basket getBasket() {
        return this.basket;
    }

    /** Returns the Customer's id. */
    public String getId() {
        return this.id;
    }

    /** Returns the Store ID where the Customer is located.  */
    public String getStore() {
        if (this.store == null) {
            return null;
        }

        return this.store.getId();
    }

    /** @param basket The Basket to associate with the Customer. */
    public void setBasket(Basket basket) {
        this.basket = basket;
    }

    /**
     * Updates the location of the Customer, and the time the change occured.
     * @param store     The Store where the Customer was seen.
     * @param aisle     The Aisle where the Customer was seen.
     * @param time      The time which the observation was made.
     */
    public void setLocation(Store store, Aisle aisle, String time) {
        this.store = store;
        this.aisle = aisle;
        this.lastSeen = time;
    }

    /** toString() helper method to output Customer's first and last name. */
    public String customerName() {
        return this.firstName + " " + this.lastName;
    }

    /** toString() helper method to output Customer's current location. */
    public String customerLocation() {
        String location = "Currently located @ ";
        location += (this.store == null) ? "N/a" : this.store.getName();
        location += (this.aisle == null) ? "" : this.aisle.getId();
        return location;
    }

    public Integer calculateBasketTotal() {
        return 42;
    }

    public String getAccountAddress() {
        return this.accountAddress;
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
        customer += "\tLast seen: ";
        customer += (this.lastSeen == null) ? "N/a" : this.lastSeen;
        customer += "\n";
        customer += String.format("\tType: %s\n", this.type);
        customer += String.format("\tEmail: %s\n", this.email);
        customer += String.format("\tAccount address: %s", this.accountAddress);

        return customer;
    }
}