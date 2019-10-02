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
        this.location = checkLocation(location);
        this.shelfList = new ArrayList<Shelf>();
    }

    private Location checkLocation(String location) {
        if (location.equals("floor")) {
            return Location.FLOOR;
        } else {
            return Location.STOCK_ROOM;
        }
    }

    public Integer getNumber() {
        return this.number;
    }
    public String getLocation() {
        return this.location.toString();
    }

    public String toString() {
        return String.format("Aisle #%d [%s] -- %s (%s)",
                this.number,
                this.location,
                this.name,
                this.description
        );

    }
}