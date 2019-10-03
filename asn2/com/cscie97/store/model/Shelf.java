package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Shelf {

    private String id;
    private String name;
    private Level level;
    private String description;
    private Temperature temperature;
    private List<Inventory> inventoryList;

    public Shelf (String id, String name, String level, String description, String temperature) {
        this.id = id;
        this.name = name;
        this.level = checkLevel(level);
        this.description = description;
        this.temperature = checkTemperature(temperature);

        this.inventoryList = new ArrayList<Inventory>();
    }

    private Temperature checkTemperature(String temperature) {
        switch(temperature) {
            case "frozen":
                return Temperature.FROZEN;
            case "refrigerated":
                return Temperature.REFRIGERATED;
            case "warm":
                return Temperature.WARM;
            case "hot":
                return Temperature.HOT;
            default:
                return Temperature.AMBIENT;
        }
    }

    private Level checkLevel(String level) {

        switch(level) {
            case "low":
                return Level.LOW;
            case "medium":
                return Level.MEDIUM;
            case "high":
                return Level.HIGH;
            default:
                return Level.MEDIUM;
        }
    }

    public void addInventory(Inventory inventory) {
        this.inventoryList.add(inventory);
    }
    public String getId() {
        return this.id;
    }

    public Inventory getInventory(String inventoryId) {
        Iterator<Inventory> inventories = this.inventoryList.iterator();

        while(inventories.hasNext()) {
            Inventory inventory = inventories.next();

            if(inventoryId.equals(inventory.getId())) {
                return inventory;
            }
        }

        return null;
    }

    public String toString() {
        String shelf;

        shelf = String.format("| Shelf #%s -- %s\n", this.id, this.name);
        shelf += String.format("|\t Description: %s\n", this.description);
        shelf += String.format("|\t Temperature: %s\n", this.temperature);
        shelf += String.format("|\t Level: %s\n", this.level);

        return shelf;
    }
}