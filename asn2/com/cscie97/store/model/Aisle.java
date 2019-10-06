package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Aisle {
    /** Name of the Aisle (e.g. Dairy) */
    private String name;

    /** Aisle ID (number). */
    private String id;

    /** Description of Aisle (e.g. ) */
    private String description;

    /** Location of Aisle (Floor or Stock Room) */
    private Location location;

    /** List of Shelves located in the Aisle. */
    private List<Shelf> shelfList;

    /**
     * Aisle Constructor
     *
     * @param aisleNumber
     * @param name
     * @param description
     * @param location
     */
    public Aisle (String aisleId, String name, String description, Location location) {
        this.id = aisleId;
        this.name = name;
        this.description = description;
        this.location = (location == null) ? Location.FLOOR : location;
        this.shelfList = new ArrayList<Shelf>();
    }

    public String getId() {
        return this.id;
    }
    public String getLocation() {
        return this.location.toString();
    }

    public List<Shelf> getShelfList() {
        return this.shelfList;
    }

    public Shelf getShelf(String id){
//        List<Shelf> shelves = getShelfList();

        for (Shelf shelf : getShelfList()) {
            if(id.equals(shelf.getId())) {
                return shelf;
            }
        }

        return null;
    }

    public void addShelf(Shelf shelf) {
        this.shelfList.add(shelf);
    }

    /**
     * Override default toString method.
     *
     * Displays details of the aisle, including the name, description and
     * list of shelves.
     */
    public String toString() {

        String aisle;

        aisle = String.format("| Aisle #%s -- %s\n", this.id, this.name);
        aisle += String.format("|\t Location: %s\n", this.location);
        aisle += String.format("|\t Description: %s\n", this.description);
        aisle += String.format("|\t Shelves:\n");
        for(Shelf shelf : getShelfList()) {
            aisle += shelf;
        }

        return aisle;
    }
}