package com.cscie97.store.model;

/**
 * An association class that references a Product and stores a given count
 * of that Product. The count can be increased or decreased based on events
 * detected in the Store.
 *
 * The ProductAssociation is extended by Basket and Inventory and are used to
 * update the count. Behavior for what happens when a count equals or goes below
 * 0, or goes above a certain threshold, varies and is dependent on the child
 * class.
 *
 * @author Matthew Thomas
 */
public class ProductAssociation {
    /** The current number of the Product stored. */
    private Integer count;

    /** The Product being tracked. */
    private Product product;

    /**
     * ProductAssociation Constructor.
     *
     * Makes an association between the class which creates the ProductAssociation
     * and a Product object. It initializes 'count' to the number specified.
     *
     * @param count
     * @param product
     */
    public ProductAssociation(Integer count, Product product) {
        this.count = count;
        this.product = product;
    }

    /** Returns the Product's id. */
    public String getProductId() {
        return this.product.getId();
    }

    /** Returns the name of the Product. */
    public String getProductName() {
        return this.product.getName();
    }

    /** Returns the cost of the Product. */
    public Integer getCost() {
        return this.product.getUnitPrice() * this.count;
    }

    /** Returns the current count of Product. */
    public Integer getCount() {
        return this.count;
    }

    /** @param countChange The amount to increment the count by. */
    public void updateCount(Integer countChange) {
        this.count += countChange;
    }

    /** @param countChange The amount to decrement the count by. */
    public void decrementCount(Integer countChange) {
        this.count -= countChange;
    }

    /**
     * Override default toString method.
     *
     * Displays the product name and the current count.
     */
    public String toString() {
        return String.format("Product: %s (%d)", getProductName(), getCount());
    }

}