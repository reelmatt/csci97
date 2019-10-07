package com.cscie97.store.model;

public class Product {

    private String id;
    private String name;
    private String description;
    private String size;
    private String category;
    private Double unitPrice;
    private Temperature temperature;

    public Product (String id,
                    String name,
                    String description,
                    String size,
                    String category,
                    Double price,
                    Temperature temperature) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.size = size;
        this.category = category;
        this.unitPrice = price;
        this.temperature = (temperature == null) ? Temperature.AMBIENT : temperature;
    }

    public String getId() {
        return this.id;
    }
    public String toString() {
        return "Product #" + this.id + " -- " + this.name;
    }
}