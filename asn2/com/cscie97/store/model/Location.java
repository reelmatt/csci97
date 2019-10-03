package com.cscie97.store.model;

public enum Location {
    FLOOR,
    STOCK_ROOM;

    public static Location getType(String choice) {
        for (Location location : Location.values()) {
            if (choice.equals(location.toString().toLowerCase())) {
                return location;
            }
        }

        return null;
    }

}