package com.cscie97.store.model;

public class ProductAssociation {
    private Integer count;
    private Product product;

    public ProductAssociation(Integer count, Product product) {
        this.count = count;
        this.product = product;
    }
}