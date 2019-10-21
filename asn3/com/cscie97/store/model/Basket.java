package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A basket is associated with a Store 24X7 Customer and tracks the Products,
 * and amounts, that they add during their shopping trip. The Basket can add
 * and remove Products (with specified amounts) and clear all Products currently
 * contained within it. A Basket is directly linked to one Customer and uses the
 * same reference ID.
 *
 * @author Matthew Thomas
 */
public class Basket {
    /** Basket ID (matches the customer id). */
    private String id;

    /** List of Products (and count) the basket contains. */
    private List<ProductAssociation> productList;

    /**
     * Basket Constructor
     *
     * Creates a Basket that is associated with a Customer. The Basket starts
     * with an empty list of ProductAssociations.
     *
     * @param id  The basket identifier (matches the customer id to whom the
     *            basket belongs).
     */
    public Basket (String id) {
        this.id = id;
        this.productList = new ArrayList<ProductAssociation>();
    }

    /** @param product  The ProductAssociation (product and count) to add. */
    public void addItem(ProductAssociation product) {
        this.productList.add(product);
    }

    /** Clear the product list. */
    public void clear() {
        this.productList.clear();
    }

    /**
     * Locate a given item within the Basket.
     *
     * @param   basketItemId    The Product to search for in the Basket.
     * @return                  Requested Product, and its count, if it exists.
     *                          Otherwise, null.
     */
    public ProductAssociation getBasketItem(String basketItemId) {
        for (ProductAssociation item : getBasketItems()) {
            if (basketItemId.equals(item.getProductId())) {
                return item;
            }
        }

        return null;
    }

    /** Returns the list of Products in the basket. */
    public List<ProductAssociation> getBasketItems() {
        return this.productList;
    }

    /** Returns the Basket's id. */
    public String getId() {
        return this.id;
    }

    /**
     * Remove the ProductAssociation from the Basket (product and entire count).
     *
     * @param   item   The ProductAssociation to remove.
     * @return         True, if successfully removed. False, if error occured.
     */
    public boolean removeItem(ProductAssociation item) {
        return this.productList.remove(item);
    }

    /**
     * Override default toString method.
     */
    public String toString() {
        return "Basket #" + this.id;
    }
}