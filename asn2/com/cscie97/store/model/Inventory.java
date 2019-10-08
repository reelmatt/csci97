package com.cscie97.store.model;

/**
 * Inventory tracks the number of Products remaning on a Store Shelf.
 *
 *
 *
 * @author Matthew Thomas
 */
public class Inventory extends ProductAssociation {
    /** Inventory ID. */
    private String id;

    /** Total capacity of the Inventory. */
    private Integer capacity;

    /** Current count of Products remaining. */
    private Integer count;

    /** Product tracked by the Inventory. */
    private Product product;

    /**
     * Inventory Constructor.
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

    /** */
    public Integer getCapacity() {
        return this.capacity;
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
        inventory += String.format("\tCount: %d", this.count);
        return inventory;
    }
}