package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Aisle {

    private String name;
    private Integer number;
    private String description;
    private Location location;
    private List<Shelf> shelfList;

    public Aisle (Integer aisleNumber, String name, String description, String location) {
        this.number = aisleNumber;
        this.name = name;
        this.description = description;
        this.location = Location.getType(location);
        this.shelfList = new ArrayList<Shelf>();
    }

    public Integer getNumber() {
        return this.number;
    }
    public String getLocation() {
        return this.location.toString();
    }

//    public List<Shelf> getShelves() {
//        return this.shelfList;
//    }

    private Iterator<Shelf> listShelves() {
        return this.shelfList.iterator();
    }

    public Shelf getShelf(String id){
        Iterator<Shelf> shelves = listShelves();

        while(shelves.hasNext()) {
            Shelf shelf = shelves.next();

            if(id.equals(shelf.getId())) {
                return shelf;
            }
        }

        return null;
    }

    public void showShelves() {
        Iterator<Shelf> shelves = listShelves();

        while(shelves.hasNext()) {
            Shelf shelf = shelves.next();

            System.out.println(shelf);
        }

        return;
    }

    public String printShelves() {
        Iterator<Shelf> shelves = listShelves();
        String output = "";
        while(shelves.hasNext()) {
            Shelf shelf = shelves.next();
            output += shelf;
//            System.out.println(shelf);
        }

        return output;
    }

    public void addShelf(Shelf shelf) {
        this.shelfList.add(shelf);
    }

    public String toString() {

        String aisle;

        aisle = String.format("| Aisle #%d -- %s\n", this.number, this.name);
        aisle += String.format("|\t Location: %s\n", this.location);
        aisle += String.format("|\t Description: %s\n", this.description);
        aisle += String.format("|\t Shelves:\n%s", printShelves());


        return aisle;
    }
}