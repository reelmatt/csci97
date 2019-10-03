package com.cscie97.store.model;

public class Inventory extends ProductAssociation {
    private String id;
    private Integer capacity;
    private Integer count;
    private Product product;

    public Inventory (String id, Integer capacity, Integer count, Product product) {
        super(count, product);
        this.id = id;
        this.capacity = capacity;
//        this.count = count;
//        this.product = product;
    }

    public String getId() {
        return this.id;
    }

    public String toString() {
        return "Inventory #" + this.id + " -- capacity " + this.capacity;
    }
}