package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

public class Sensor extends Device {

//    private enum Type {CAMERA, MICROPHONE};
    private SensorType type;

    public Sensor (String id, String name, String type, Aisle location) {
        super(id, name, location);
        this.type = SensorType.CAMERA;
    }

    public String toString() {
        String sensor;

        sensor = String.format("| Device %s -- %s\n", this.getId(), this.getName());
        sensor += String.format("|\tType: %s\n", this.type);
        sensor += String.format("|\tLocation: Aisle %d\n", this.getLocation().getNumber());

        return sensor;
    }
}