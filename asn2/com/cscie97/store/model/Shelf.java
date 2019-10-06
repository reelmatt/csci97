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

    public Shelf (String id, String name, Level level, String description, Temperature temperature) {
        this.id = id;
        this.name = name;
        this.level = (level == null) ? Level.HIGH : level;
        this.description = description;
        this.temperature = (temperature == null) ? Temperature.AMBIENT : temperature;
        this.inventoryList = new ArrayList<Inventory>();
    }



    public void addInventory(Inventory inventory) {
        this.inventoryList.add(inventory);
    }

    public String getId() {
        return this.id;
    }

    public List<Inventory> getInventoryList() {
        return this.inventoryList;
    }

    public Inventory getInventory(String inventoryId) {
        for (Inventory inventory : getInventoryList()) {
            if(inventoryId.equals(inventory.getId())) {
                return inventory;
            }
        }

        return null;

//        Iterator<Inventory> inventories = this.inventoryList.iterator();
//
//        while(inventories.hasNext()) {
//            Inventory inventory = inventories.next();
//
//            if(inventoryId.equals(inventory.getId())) {
//                return inventory;
//            }
//        }

//        return null;
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