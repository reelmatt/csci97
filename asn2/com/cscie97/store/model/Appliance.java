package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

public class Appliance extends Device {

    private ApplianceType type;

    public Appliance (String id, String name, ApplianceType type, Aisle location) {
        super(id, name, location);
        this.type = type;

    }

    public void respondToCommand() {
        System.out.println("Responding to Command...");
    }

    public String toString() {
        String appliance;

        appliance = String.format("| Device %s -- %s\n", this.getId(), this.getName());
        appliance += String.format("|\tType: %s\n", this.type);
        appliance += String.format("|\tLocation: Aisle %d\n", this.getLocation().getNumber());

        return appliance;
    }
}