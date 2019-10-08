package com.cscie97.store.model;

/**
 *
 *
 * @author Matthew Thomas
 */
public class ProductAssociation {
    /** */
    private Integer count;

    /** */
    private Product product;

    /**
     * ProductAssociation Constructor.
     *
     *
     *
     * @param count
     * @param product
     */
    public ProductAssociation(Integer count, Product product) {
        this.count = count;
        this.product = product;
    }

    /** Returns the current count of Product. */
    public Integer getCount() {
        return this.count;
    }

//    public void updateCount(Integer itemCount) {
//        this.count += itemCount;
//    }

    /** Returns the Product's id. */
    public String getProductId() {
        return this.product.getId();
    }

    /** Returns the name of the Product. */
    public String getProductName() {
        return this.product.getName();
    }

    public void updateCount(Integer countChange) {
        this.count += countChange;
    }

    public void decrementCount(Integer countChange) {
        this.count -= countChange;
    }

    public String toString() {
        return String.format("Product: %s (%d)", getProductName(), getCount());

    }

}