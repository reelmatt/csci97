package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

public class Shelf {
    private enum Level {LOW, MEDIUM, HIGH};
    private enum Temperature {FROZEN, REFRIGERATED, AMBIENT, WARM, HOT};

    private Integer id;
    private String name;
    private Level level;
    private String description;
    private Temperature temperature;
    private List<Inventory> inventoryList;

    public Shelf (Integer id, String name, String level, String description, String temperature) {
        this.id = id;
        this.name = name;
        this.level = Level.HIGH;
        this.description = description;
        this.temperature = Temperature.AMBIENT;

        this.inventoryList = new ArrayList<Inventory>();
    }

    public String toString() {
        return "Shelf #" + this.id + " -- " + this.name;
    }
}