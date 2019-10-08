package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An individual Shelf, which is located within an Aisle within a Store and
 * tracked by the Store Model Service.
 *
 * A Shelf contains information about
 *
 * @author Matthew Thomas
 */
public class Shelf {
    /** Shelf ID */
    private String id;

    /** Name of the Shelf (e.g. ) */
    private String name;

    /** Level of the Shelf (low | medium | high) */
    private Level level;

    /** Description of Shelf (e.g. ) */
    private String description;

    /** Temperature Shelf is kept at. */
    private Temperature temperature;

    /** List of Inventory objects located on the Shelf. */
    private List<Inventory> inventoryList;

    /**
     * Shelf Constructor
     *
     * Creates a Shelf, which is added by the Store Model Service to a
     * corresponding Aisle, withing a given Store. Shelves are unique within
     * a given aisle, but are not required to be unique between Aisles or
     * between Stores. All properties are required to be specified, except for
     * Temperature, which will be assigned "AMBIENT" if excluded from the
     * parameters.
     *
     * @param id            The shelf identifier.
     * @param name          Name of the Shelf.
     * @param level         Height of the Shelf.
     * @param description   Description of what is stored on the Shelf.
     * @param temperature   Temperature items on the Shelf are kept at.
     */
    public Shelf (String id, String name, Level level, String description, Temperature temperature) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.description = description;
        this.temperature = (temperature == null) ? Temperature.AMBIENT : temperature;
        this.inventoryList = new ArrayList<Inventory>();
    }

    /** Returns the Shelf's identifier. */
    public String getId() {
        return this.id;
    }

    /** @param inventory The Inventory to add to the Shelf. */
    public void addInventory(Inventory inventory) {
        this.inventoryList.add(inventory);
    }

    /** Returns a list of Inventory objects located on the Shelf. */
    public List<Inventory> getInventoryList() {
        return this.inventoryList;
    }

    /**
     * Located a given Inventory on the Shelf.
     *
     * @param   inventoryId   The Inventory to find.
     * @return  Requested Inventory, if exists. Otherwise, null.
     */
    public Inventory getInventory(String inventoryId) {
        for (Inventory inventory : getInventoryList()) {
            if(inventoryId.equals(inventory.getId())) {
                return inventory;
            }
        }

        return null;
    }

    /**
     * Override default toString method.
     *
     * Displays details of the shelf, including the id, name, level, description
     * and temperature.
     */
    public String toString() {
        String shelf;

        shelf = String.format("Shelf #%s -- %s\n", this.id, this.name);
        shelf += String.format("\t Description: %s\n", this.description);
        shelf += String.format("\t Temperature: %s\n", this.temperature);
        shelf += String.format("\t Level: %s\n", this.level);

        return shelf;
    }
}