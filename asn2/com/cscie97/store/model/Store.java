package com.cscie97.store.model;

import java.util.List;
import java.util.ArrayList;

/**
 * An individual Store within the Store Model Service.
 *
 * A Store is globally unique within the System. Each Store contains any number
 * of Aisle(s), which in turn track Shelf, Inventory, and Product information.
 *
 * Stores also contain Devices and Customers, each of which are associated with
 * a specific location within the Store, but those lists are tracked by the
 * StoreModelService and operated by the StoreController (forthcoming in
 * assignment 3).
 *
 * @author Matthew Thomas
 */
public class Store {
    /** Store ID */
    private String id;

    /** Name of the Store */
    private String name;

    /** Physical address of the Store. */
    private String address;

    /** A List of Aisles in the Store. */
    private List<Aisle> aisleList;

    /**
     * Store Constructor
     *
     * Creates a Store with a globally unique identifier. Each Store starts
     * with an empty list of Aisles.
     *
     * @param id        Globally unique identifier.
     * @param name      Name of the Store.
     * @param address   Address of the Store.
     */
    public Store (String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.aisleList = new ArrayList<Aisle>();
    }

    /** Returns the Store's unique identifier. */
    public String getId() {
        return this.id;
    }

    /** Returns the name of the Store. */
    public String getName() {
        return this.name;
    }

    /** Returns the address where the Store is located. */
    public String getAddress() {
        return this.address;
    }

    /**
     * Locate the given Aisle within the Store's aisleList.
     *
     * @param   aisleId     The Aisle to find.
     * @return              Requested Aisle if exists. Otherwise, null.
     */
    public Aisle getAisle(String aisleId) {
        for (Aisle aisle : getAisleList()) {
            if (aisleId.equals(aisle.getId())) {
                return aisle;
            }
        }

        return null;
    }

    /** Returns a list of Aisles located within the Store. */
    public List<Aisle> getAisleList() {
        return this.aisleList;
    }

    /** @param aisle The aisle to add to the Store's list. */
    public void addAisle(Aisle aisle) {
        this.aisleList.add(aisle);
    }

    /**
     * Override default toString method.
     *
     * Displays details of the store including the id, name, and address.
     */
    public String toString() {
        String store = this.id + "\n";
        store += String.format("Name: %s\n", this.name);
        store += String.format("Address: %s", this.address);

        return store;
    }
}