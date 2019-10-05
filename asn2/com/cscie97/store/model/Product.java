package com.cscie97.store.model;

public class Product {
    private enum Temperature {FROZEN, REFRIGERATED, AMBIENT, WARM, HOT};

    private String id;
    private String name;
    private String description;
    private Integer size;
    private String category;
    private Double unitPrice;
    private Temperature temperature;

    public Product (String id, String name, String description, Integer size, String category, Double price, String temperature) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.size = size;
        this.category = category;
        this.unitPrice = price;
        this.temperature = Temperature.AMBIENT;
    }

    public String toString() {
        return "Product #" + this.id + " -- " + this.name;
    }
}