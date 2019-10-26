package com.cscie97.store.model;

/**
 * A Product known by the Store 24X7 System.
 *
 * Products are objects that are located in Inventory, on a Shelf, in an Aisle,
 * and within a Store. Products can be added to an Inventory as the Store
 * receives more supply, or removed from Inventory and place in Customer's
 * Baskets as they shop at the Store. Registered Customers can purchase the
 * products contained within their Basket when they leave the Store.
 *
 * @author Matthew Thomas
 */
public class Product {
    /** The Product identifier (aka SKU). */
    private String id;

    /** Name of the Product (e.g. Coca Cola). */
    private String name;

    /** Description of the Product (e.g. Carbonated soft drink). */
    private String description;

    /** Size of the product, either weight/volume (e.g. 12 fluid ounces). */
    private String size;

    /** Product category (e.g. Beverage). */
    private String category;

    /** Price of a single item. */
    private Integer unitPrice;

    /** Temperature the Product is stored at. */
    private Temperature temperature;

    /**
     * Product Constructor.
     *
     * Creates a Product that is added to the Store Model Service. Product
     * information is obtained, usually through the third-party vendor who
     * makes the Product. All information is required, except for the
     * 'temperature', which has a default value of "AMBIENT".
     *
     * @param id            The Product identifier (aka SKU).
     * @param name          Name of the Product.
     * @param description   Description of the Product.
     * @param size          Size (weight/volume).
     * @param category      Product category.
     * @param price         Unit price.
     * @param temperature   Temperature the Product is stored at.
     */
    public Product (String id,
                    String name,
                    String description,
                    String size,
                    String category,
                    Integer price,
                    Temperature temperature) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.size = size;
        this.category = category;
        this.unitPrice = price;
        this.temperature = (temperature == null) ? Temperature.AMBIENT : temperature;
    }

    /** Returns the Product's ID (SKU). */
    public String getId() {
        return this.id;
    }

    /** Returns the name of the Product. */
    public String getName() {
        return this.name;
    }

    /** Get the unit price of the product. */
    public Integer getUnitPrice() {
        return unitPrice;
    }

    /** Returns the Product's temperature. */
    public Temperature getTemperature() {
        return temperature;
    }

    /**
     * Override default toString method.
     *
     * Displays details of the product, including the id, name, description, size,
     * category, price, and temperature.
     */
    public String toString() {
        String product;

        product = String.format("Product '%s': %s\n", this.id, this.name);
        product += String.format("\t Description: %s\n", this.description);
        product += String.format("\t Size: %s\n", this.size);
        product += String.format("\t Category: %s\n", this.category);
        product += String.format("\t Unit Price: %d\n", this.unitPrice);
        product += String.format("\t Temperature: %s\n", this.temperature);

        return product;
    }
}