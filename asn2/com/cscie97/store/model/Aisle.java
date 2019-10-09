package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An individual Aisle, which is located within a Store and tracked by the
 * Store Model Service.
 *
 * An Aisle stores a list of Shelf objects which break down further to ultimately
 * track Products within a Store. The Aisle is also where Customers can walk and
 * Devices (Sensors and Appliances) can monitor activity and also respond to
 * commands (Appliances only). An Aisle can be located either on the 'floor'
 * (which is accessible by all) or the 'stock_room' (which is not accessible by
 * customers).
 *
 * @author Matthew Thomas
 */
public class Aisle {
    /** Aisle ID (number). */
    private String id;

    /** Name of the Aisle (e.g. Product) */
    private String name;

    /** Description of Aisle (e.g. fruits and vegetables) */
    private String description;

    /** Location of Aisle (Floor or Stock Room) */
    private Location location;

    /** List of Shelves located in the Aisle. */
    private List<Shelf> shelfList;

    /**
     * Aisle Constructor
     *
     * Creates an Aisle that is then added by the Store Model Service to a
     * corresponding Store. Aisles are unique within a given Store, but are not
     * required to be unique between Stores.
     *
     * @param aisleId       The identifier, aka 'number' of the Aisle.
     * @param name          Name of the Aisle.
     * @param description   Description of what is contained in the Aisle.
     * @param location      Place where Aisle is located within the Store.
     */
    public Aisle (String aisleId, String name, String description, Location location) {
        this.id = aisleId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.shelfList = new ArrayList<Shelf>();
    }

    /** Returns the Aisle's identifier (or number). */
    public String getId() {
        return this.id;
    }

    /** Returns the location within a Store where the Aisle is located. */
    public String getLocation() {
        return this.location.toString();
    }

    /**
     * Locate a given Shelf within the Aisle.
     *
     * @param   id      The Shelf to find.
     * @return          Requested Shelf, if exists. Otherwise, null.
     */
    public Shelf getShelf(String shelfId){
        for (Shelf shelf : getShelfList()) {
            if(shelfId.equals(shelf.getId())) {
                return shelf;
            }
        }

        return null;
    }

    /** Returns a list of Shelf objects located in the Aisle. */
    public List<Shelf> getShelfList() {
        return this.shelfList;
    }

    /** @param  shelf   The Shelf to add to the Aisle's list. */
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

        aisle = String.format("Aisle #%s -- %s\n", this.id, this.name);
        aisle += String.format("Location: %s\n", this.location);
        aisle += String.format("Description: %s\n", this.description);
        aisle += String.format("Shelves:\n");

        List<Shelf> shelves = getShelfList();
        if (shelves.size() == 0) {
            aisle += "\tThe store has 0 shelves.";
        } else {
            for(Shelf shelf : shelves) {
                aisle += "    " + shelf;
            }
        }

        return aisle;
    }
}