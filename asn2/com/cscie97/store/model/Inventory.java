package com.cscie97.store.model;

public class Inventory {
    private Integer id;
    private Integer capacity;
    private Integer count;
    private Product product;

    public Inventory (Integer id, Integer capacity, Integer count, Product product) {
        this.id = id;
        this.capacity = capacity;
        this.count = count;
        this.product = product;
    }

    public String toString() {
        return "Inventory #" + this.id + " -- capacity " + this.capacity;
    }
}