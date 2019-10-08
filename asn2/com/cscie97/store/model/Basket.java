package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 *
 * @author Matthew Thomas
 */
public class Basket {
    /** */
    private String id;

    /** */
    private List<ProductAssociation> productList;

    /**
     * Basket Constructor
     *
     *
     *
     * @param id
     */
    public Basket (String id) {
        this.id = id;
        this.productList = new ArrayList<ProductAssociation>();
    }

    /** Returns the Basket's id. */
    public String getId() {
        return this.id;
    }

    /** @param product  The ProductAssociation (product and count) to add. */
    public void addItem(ProductAssociation product) {
        this.productList.add(product);
    }

    /** */
    public ProductAssociation removeItem(String productId, Integer itemCount) {
        ProductAssociation product = getBasketItem(productId);

        if (product == null) {
            return null;
        }

        if ((product.getCount() + itemCount) < 0) {
            return null;
        }
        product.setCount(itemCount);
        return product;
    }

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

    public void clear() {
        return;
    }

    /**
     * Override default toString method.
     */
    public String toString() {
        return "Basket #" + this.id;
    }
}