package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

public class Aisle {
    private enum Location {FLOOR, STOCK_ROOM};

    private String name;
    private Integer number;
    private String description;
    private Location location;
    private List<Shelf> shelfList;

    public Aisle (Integer aisleId, String name, String description, String location) {
        this.number = aisleId;
        this.name = name;
        this.description = description;
        this.location = Location.FLOOR;
        this.shelfList = new ArrayList<Shelf>();
    }

    public String toString() {
        return "Aisle #" + this.number + " -- " + this.name;
    }
}