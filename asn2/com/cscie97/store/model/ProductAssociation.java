package com.cscie97.store.model;

public class ProductAssociation {
    private Integer count;
    private Product product;

    public ProductAssociation(Integer count, Product product) {
        this.count = count;
        this.product = product;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer itemCount) {
        this.count += itemCount;
    }

    public String getProductId() {
        return this.product.getId();
    }

    public String getProductName() {
        return this.product.getName();
    }
    public void updateCount(Integer countChange) {
        this.count += countChange;
    }

}