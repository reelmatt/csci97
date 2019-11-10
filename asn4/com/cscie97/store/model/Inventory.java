package com.cscie97.store.model;

/**
 * An inventory tracks the number of Products remaning on a given shelf within
 * a store.
 *
 * An inventory extends a ProductAssociation and adds two properties: id and
 * capacity. The id can be added to a fully-qualified location string of the
 * form
 *      <store>:<aisle>:<shelf>:<inventory>
 * to locate the Inventory within the Store 24X7 System. The capacity must
 * remain >= 0 and <= capacity to be valid. This is ensured by the Store Model
 * Service in response to events detected by Devices within a Store.
 *
 * @author Matthew Thomas
 */
public class Inventory extends ProductAssociation {
    /** Inventory ID. */
    private String id;

    /** Total capacity of the Inventory. */
    private Integer capacity;

    /**
     * Inventory Constructor.
     *
     * Creates a ProductAssociation object with the added properties of
     * id and capacity.
     *
     * @param id        The inventory identifier.
     * @param capacity  Total capacity the inventory can hold.
     * @param count     Current number of the Product left in the inventory.
     * @param product   The Product being tracked.
     */
    public Inventory (String id, Integer capacity, Integer count, Product product) {
        super(count, product);
        this.id = id;
        this.capacity = capacity;
    }

    /** Returns the Inventory ID. */
    public String getId() {
        return this.id;
    }

    public Integer getCount() {
        return super.getCount();
    }

    /** Returns the Inventory capacity. */
    public Integer getCapacity() {
        return this.capacity;
    }

    public String getProductId() {
        return super.getProductId();
    }
    /**
     * Override default toString method.
     *
     * Displays details of the inventory, including the id, product name,
     * capacity, and count.
     */
    public String toString() {
        String inventory;

        inventory = String.format("Inventory '%s': %s", this.id, super.getProductName());
        inventory += String.format("\tCapacity: %d", this.capacity);
        inventory += String.format("\tCount: %d", super.getCount());

        return inventory;
    }
}