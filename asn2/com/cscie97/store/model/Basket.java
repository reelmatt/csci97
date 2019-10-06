package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

public class Basket {
    private String id;

    private List<ProductAssociation> productList;

    public Basket (String id) {
        this.id = id;
        this.productList = new ArrayList<ProductAssociation>();
    }

    public String getId() {
        return this.id;
    }

    public void addItem(ProductAssociation product) {
        this.productList.add(product);
    }

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
    public List<ProductAssociation> getBasketItems() {
        return this.productList;
    }
    public void clear() {
        return;
    }
    public String toString() {
        return "Basket #" + this.id;
    }
}